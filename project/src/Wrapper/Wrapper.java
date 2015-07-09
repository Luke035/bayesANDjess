package Wrapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.recommenders.jayes.BayesNet;
import org.eclipse.recommenders.jayes.BayesNode;
import org.eclipse.recommenders.jayes.inference.IBayesInferrer;
import org.eclipse.recommenders.jayes.inference.LikelihoodWeightedSampling;
import org.eclipse.recommenders.jayes.inference.RejectionSampling;
import org.eclipse.recommenders.jayes.inference.jtree.JunctionTreeAlgorithm;

public class Wrapper {
	private BayesNet net;
	private Map<String, BayesNode> nodes;
	private List<String> nodesList;
	private Map<BayesNode, String> evidence;
	private IBayesInferrer inferer;
	private int samplingCount;
	private Results result;

	public Wrapper(){
		net = new BayesNet();
		nodes = new HashMap<String,BayesNode>();
		nodesList = new ArrayList<String>();
		samplingCount = 2000;
	}

	public int getSamplingCount() {
		return samplingCount;
	}

	public void setSamplingCount(int samplingCount) {
		this.samplingCount = samplingCount;
	}

	/**
	 * Adds node to the net and configures the outcomes
	 * @param name - Node name
	 * @param outcomes - String[] of outcomes
	 * @param probabilities - double[] of probabilities
	 * @throws JayesException error on wrong parameters counter
	 */
	public void addNode(String name, String[] outcomes, 
			double[] probabilities) throws JayesException{
		addNode(name, outcomes, new String[]{}, probabilities);
	}

	/**
	 * Adds node to the net and configures the outcomes
	 * @param name - Node name
	 * @param outcomes - String[] of outcomes
	 * @throws JayesException error on wrong parameters counter
	 */
	public void addNode(String name, String[] outcomes) throws JayesException{
		addNode(name, outcomes, new String[]{});
	}

	/**
	 * Adds node to the net and configures the outcomes and parents 
	 * The probabilities are set with equal values (Principle of Indifference)
	 * @param name - Node name
	 * @param outcomes - String[] of outcomes
	 * @param parents - String[] of parent nodes
	 * @throws JayesException error on wrong parameters counter
	 */
	public void addNode(String name, String[] outcomes, 
			String[] parents) throws JayesException{

		int laPlaceLength = outcomes.length;

		if (parents.length!=0){
			if (! nodeExists(parents)){
				throw new JayesException("One or more parents of '" + name + "' don't exist.");
			}
			List<BayesNode> parentList = getNodes(parents);
			for (BayesNode p:parentList){
				laPlaceLength = laPlaceLength * p.getOutcomeCount();
			}
		}

		double[] laPlace = new double[laPlaceLength];
		for(int i=0; i<outcomes.length; i++){
			laPlace[i] = 1.0 / outcomes.length;
		}
		addNode(name, outcomes, parents, laPlace);
	}

	/**
	 * Adds node to the net and configures the outcomes, parents and probabilities
	 * @param name - Node name
	 * @param outcomes - String[] of outcomes
	 * @param parents - String[] of parent nodes
	 * @param probabilities - double[] of probabilities
	 * @throws JayesException error on wrong parameters counter or non existent parents
	 */
	public void addNode(String name, String[] outcomes, 
			String[] parents, double[] probabilities) throws JayesException{
		// checks if new node
		if (nodeExists(name)) {
			throw new JayesException("Node '" + name + "' already exist.");
		}
		int prob = probabilities.length;
		int expected = outcomes.length;

		// check for parent existence
		List<BayesNode> parentList = null;
		if (parents.length!=0){
			if (! nodeExists(parents)){
				throw new JayesException("One or more parents of '" + name + "' don't exist.");
			}
			parentList = getNodes(parents);
			for (BayesNode n:parentList){			
				expected *= n.getOutcomeCount();
			}
		}
		// check for correct parameters size
		if (expected != prob) {
			throw new JayesException("Incorrect number of probabilities for '" + name + 
					"' (Expected " + expected + ", Given " + prob + ").");
		}
		// create the bayesian net
		BayesNode newNode = net.createNode(name);
		newNode.addOutcomes(outcomes);
		if (parents.length!=0){
			newNode.setParents(parentList);
		}
		newNode.setProbabilities(probabilities);
		
		List<String> pOutcomes = new ArrayList<String>();
		for (String p: parents){
			String[] temp = getOutcomes(p);
			for (String o:temp)
				pOutcomes.add(o);
		}
		
		nodes.put(name.toLowerCase(), newNode);
		nodesList.add(name);
	}
	
	public NodeProbability getNodeProbability(String node) throws JayesException{
		NodeProbability nodeProbability = new NodeProbability(node);
		
		double [] prob = this.getProbabilities(node);
		String [] parents = this.getParents(node);
		String [] outcomes = this.getOutcomes(node);
		
		if(parents.length == 0){ //Nodo a priori
			for(int i=0; i<prob.length; i++){
				ComposedProbability cp = new ComposedProbability(outcomes[i],prob[i]);
				nodeProbability.addProbability(cp);
			}
			return nodeProbability;
		}
		
		
		//Array delle dimensioni dei parent
		int [] parentsDim = new int[parents.length];
		for(int i=0; i<parentsDim.length;i++){
			parentsDim[i] = this.getOutcomes(parents[i]).length;
		}
		
		int [] modulesArray = new int[parents.length];
		//Array dei moduli
		modulesArray[modulesArray.length-1] = 1; //L'ultimo è uguale, si muove sempre
		for(int i=modulesArray.length-2; i>=0; i--){
			int product = outcomes.length;
			for(int j=i+1; j<parentsDim.length; j++){
				product = product*parentsDim[j];
			}
			modulesArray[i] = product;
		}
		
		/*//Print modules array
		for(int module:modulesArray){
			System.out.println("Module: "+module);
		}*/
		
		for(int i=0; i<prob.length;i++){
			List<String> actualOutcomes = new ArrayList<String>();
			
			for(int j=0; j<parents.length-1; j++){
				String [] temporalParentOutcomes = this.getOutcomes(parents[j]);
				//System.out.println("Outcome index = "+((i/modulesArray[j])%parentsDim[j]));
				String outcomeToAdd = temporalParentOutcomes[(i/modulesArray[j])%parentsDim[j]];
				actualOutcomes.add(outcomeToAdd);
			}
			
			//Aggiunto ultimo (quello che si muove sempre)
			String [] lastParentsOutcomes = this.getOutcomes(parents[parents.length-1]);
			actualOutcomes.add(lastParentsOutcomes[(i/outcomes.length)%parentsDim[parentsDim.length-1]]);
			
			ComposedProbability cp = new ComposedProbability(
					actualOutcomes,
					parents,
					outcomes[i%outcomes.length],
					prob[i]
					);
			nodeProbability.addProbability(cp);
			
			//System.out.println(cp.toString());
		}
		
		/*List<String> pOutcomes = new ArrayList<String>();
	
		
		System.out.println("Outcomes form: "+this.getOutcomesForm(node));
		
		
		for (String p: parents){
			String[] temp = this.getOutcomes(p);
			for (String o:temp){
				System.out.println("parent: "+o);
				pOutcomes.add(o);
			}
		}
		
		for(int i=0 ;i<prob.length;i++){
			ComposedProbability cp = new ComposedProbability(
					pOutcomes.get(i%pOutcomes.size()),
					outcomes[i/pOutcomes.size()],
					prob[i]
					);
			System.out.println("Generated: "+cp.toString());
			nodeProbability.addProbability(cp);
		}*/
		
		return nodeProbability;
	}

	/**
	 * Set the new CPT of a node
	 * @param name - Node name
	 * @param probabilities - Set of probabilities
	 * @throws JayesException error on wrong parameters counter
	 */
	public void setProbabilites(String name, double[] probabilities) 
			throws JayesException{
		// checks if new node
		if (! nodeExists(name)) {
			throw new JayesException("Node '" + name + "' does not exist.");
		}
		BayesNode node = getNode(name);
		int prob = probabilities.length;
		int expected = node.getOutcomeCount();
		for (BayesNode n:node.getParents()){			
			expected *= n.getOutcomeCount();
		}
		if (expected != prob) {
			throw new JayesException("Incorrect number of probabilities for '" + name + 
					"' (Expected " + expected + ", Given " + prob + ").");
		}
		// save the cpt
		node.setProbabilities(probabilities);
	}

	public double[] getProbabilities(String nodeName) throws JayesException{
		if (! nodeExists(nodeName)){
			throw new JayesException("Node '" + nodeName + "' doesn't exist.");
		}

		return getNode(nodeName).getProbabilities();
	}

	private BayesNode getNode(String nodeName){
		return nodes.get(nodeName.toLowerCase());
	}

	public String[] getNodes(){		
		return nodesList.toArray(new String[]{});
	}

	public String[] getOutcomes(String nodeName){
		String[] out = getNode(nodeName).getOutcomes().toArray(new String[]{});
		return out;
	}

	public String getOutcomesForm(String nodeName){
		String res = "<";
		for (String out:getOutcomes(nodeName)){
			res += out + "; ";
		}
		res = res.substring(0, res.length()-2);
		return res + ">";
	}

	public String[] getParents(String nodeName){
		String[] ns = new String[getNode(nodeName).getParents().size()];
		int i = 0;
		for (BayesNode n:getNode(nodeName).getParents()){
			ns[i] = n.getName();
			i++;
		}
		return ns;
	}

	public String[] getChildren(String nodeName){
		String[] ns = new String[getNode(nodeName).getChildren().size()];
		int i = 0;
		for (BayesNode n:getNode(nodeName).getChildren()){
			ns[i] = n.getName();
			i++;
		}
		return ns;
	}

	/**
	 * Get the node object list from a list of node's name
	 * @param nodeList - list of node's name
	 * @return Array List of nodes in the net, ignore non existent names
	 */
	public List<BayesNode> getNodes(String[] nodeList){
		List<BayesNode> parentList = new ArrayList<BayesNode>();
		for (String p:nodeList){
			if (nodeExists(p)){
				parentList.add(getNode(p));
			}
		}
		return parentList;
	}

	/**
	 * Gives the presence of the nodes
	 * @param nodeList - list of node's name
	 * @return true if all nodes present, 
	 * 			false if at least one non-existent 
	 */
	public boolean nodeExists(String... nodeList){
		boolean val = true;
		for (String n:nodeList){
			val &= nodes.containsKey(n.toLowerCase());
		}
		return val;
	}

	/**
	 * Set the string list of evidences as node-value
	 * @param nodeEvidence - Array of evidences (column) with given value (rows)
	 * @throws JayesException
	 */
	public void setEvidence(String[][] nodeEvidence) throws JayesException{
		if (! nodeExists(nodeEvidence[0])){
			throw new JayesException("One or more evidence nodes don't exist.");
		}

		evidence = new HashMap<BayesNode,String>();
		for (int i=0; i<nodeEvidence[0].length; i++){
			evidence.put(getNode(nodeEvidence[0][i]), nodeEvidence[1][i]);
		}
	}

	/**
	 * Evaluates the probability distribution of the node query 
	 * given the evidence, using the selected algorithm
	 * @param nodeQuery - Name of the node to evaluate
	 * @param nodeEvidence - Array of evidences (column) with given value (rows)
	 * @param algorithm - Exact inference or approximate sampling
	 * @return Probability distribution of the input query
	 * @throws JayesException query or evidence don't exist
	 */
	public void inference(String nodeQuery, String[][] nodeEvidence, 
			InfAlgorithm algorithm) throws JayesException{
		if (! nodeExists(nodeQuery)){
			throw new JayesException("Node '" + nodeQuery + "' doesn't exist.");
		}
		setEvidence(nodeEvidence);

		switch (algorithm){
		case EXACT:
			exactInference(nodeQuery);
			break;
		case LIKELI:
			likelihoodSampling(nodeQuery);
			break;
		case REJECT:
			rejectionSampling(nodeQuery);
			break;
		default:
		}
	}

	public Results getResults(){
		return result;
	}

	private void exactInference(String nodeQuery){
		inferer = new JunctionTreeAlgorithm();
		inferer.setNetwork(net);
		inferer.setEvidence(evidence);
		
		result = new Results(nodeQuery, evidence, 
				getNode(nodeQuery).getOutcomes().toArray(new String[]{}),
				inferer.getBeliefs(getNode(nodeQuery)));
	}

	private void likelihoodSampling(String nodeQuery){
		inferer = new LikelihoodWeightedSampling();
		((LikelihoodWeightedSampling)inferer).setSampleCount(samplingCount);
		inferer.setNetwork(net);
		inferer.setEvidence(evidence);

		result = new Results(nodeQuery, evidence, 
				getNode(nodeQuery).getOutcomes().toArray(new String[]{}),
				inferer.getBeliefs(getNode(nodeQuery)));
	}

	private void rejectionSampling(String nodeQuery){		
		inferer = new RejectionSampling();
		((RejectionSampling)inferer).setSampleCount(samplingCount);
		inferer.setNetwork(net);
		inferer.setEvidence(evidence);

		result = new Results(nodeQuery, evidence, 
				getNode(nodeQuery).getOutcomes().toArray(new String[]{}),
				inferer.getBeliefs(getNode(nodeQuery)));
	}
}