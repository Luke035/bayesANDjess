package Wrapper;

import java.util.ArrayList;
import java.util.List;

public class NodeProbability {
	private String node;
	private List<ComposedProbability> probabilities;
	
	public NodeProbability(String node){
		this.node = node;
		probabilities = new ArrayList<ComposedProbability>();	
	}
	
	
	
	public String getNode() {
		return node;
	}



	public List<ComposedProbability> getProbabilities() {
		return probabilities;
	}



	public void addProbability(ComposedProbability np){
		this.probabilities.add(np);
	}
	
	public double getPriorProbabilityByOutcome(String outcome) throws ProbabilityNotFoundException{
		double toReturn = Double.MIN_VALUE;
		for(ComposedProbability cp:probabilities){
			if(cp.getNodeOutcome().equals(outcome))
				toReturn = cp.getProbability();
		}
		
		if(toReturn==Double.MIN_VALUE)
			throw new ProbabilityNotFoundException("Probability not found");
		return toReturn;
	}
	
	public void updateNoParentsNode(List<Double> probs) throws NodeWithParentsException{ 
		int i=0;
		for(ComposedProbability cp:probabilities){
			if(cp.getParents() != null)
				throw new NodeWithParentsException("Node with parents instead of no parents node");
		}
		for(ComposedProbability cp:probabilities){
			cp.setProbability(probs.get(i));
			i++;
		}
	}
	
	public double[] getProbArray(){
		List<Double> probList = new ArrayList<Double>();
		
		for(ComposedProbability cp:probabilities){
			probList.add(cp.getProbability());
		}
		
		double []toReturn = new double[probList.size()];
		for(int i=0; i<probList.size();i++){
			toReturn[i] = probList.get(i);
		}
		
		return toReturn;
	}
	
	/*public double getProb(String parentOutcome, String nodeOutcome) throws ProbabilityNotFoundException{
		double prob = Double.MIN_VALUE;
		
		for(ComposedProbability cp:probabilities){
			if(cp.getParentOutcome().equalsIgnoreCase(parentOutcome) && 
					cp.getNodeOutcome().equalsIgnoreCase(nodeOutcome)){
				prob = cp.getProbability();
			}
		}
		
		if(prob==Double.MIN_VALUE){
			throw new ProbabilityNotFoundException("Probability non found");
		}
		
		return prob;
	}*/
	
}
