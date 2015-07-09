package xml_handler;

import java.util.ArrayList;
import java.util.List;

import Wrapper.JayesException;
import Wrapper.NodeProbability;
import Wrapper.ProbabilityNotFoundException;
import Wrapper.Wrapper;
import bayesANDjess.BayesNet;

public class NetUpdater {
	public BayesNet bn;

	public NetUpdater(BayesNet bayesNet) {
		super();
		this.bn = bayesNet;
	}


	public void updateNodeWithParents(List<Observation> observationList,
			String node, double t, double gamma){

		//Costruzione struttura rete attuale
		Wrapper wrapper = bn.getWrapper();
		//Cerco tutte le nuove osservazioni legate al nodo
		List<Observation> filteredObservations = getObservationsByNodeName(node,observationList);

		/*System.out.println("Node observations");
		for(Observation obs:filteredObservations){
			System.out.println(obs.toString());
		}*/

		NodeProbability nodeProbability = null;
		//Ottenimento di nodeprobability
		try {
			//System.out.println("Node: "+node);
			nodeProbability = wrapper.getNodeProbability(node);
		} catch (JayesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double [] prob = null;
		try {
			prob = bn.getWrapper().getProbabilities(node);
		} catch (JayesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//System.out.println("Prob length: "+prob.length);

		String [] parents = bn.getWrapper().getParents(node);
		String [] outcomes = bn.getWrapper().getOutcomes(node);

		//Array delle dimensioni dei parent
		int [] parentsDim = new int[parents.length];
		for(int i=0; i<parentsDim.length;i++){
			parentsDim[i] = bn.getWrapper().getOutcomes(parents[i]).length;
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
		modulesArray[modulesArray.length-1] = outcomes.length; //L'ultimo dipende dagli outcome del nodo

		/*//Print modules array
		for(int module:modulesArray){
			System.out.println("Module actual: "+module);
		}*/

		List<Double> projectionList = new ArrayList<Double>();
		List<Double> newProbs = new ArrayList<Double>();

		for(int i=0;i<prob.length;i++){
			if(i%outcomes.length==0 && i>0){ //Normalizzazione
				double alpha = 0;
				for(double proj:projectionList){
					alpha+=proj;
				}
				for(double proj:projectionList){
					double toAdd = proj/alpha;
					newProbs.add(toAdd);
					//System.out.println("New prob, node:"+node+" value:"+toAdd);
				}
				projectionList.clear(); //Ora sono aggiunti, si deve ripartire
			}

			List<Parent> parentsConfig = new ArrayList<Parent>();
			for(int j=0;j<parents.length;j++){
				String [] temporalParentOutcomes = bn.getWrapper().getOutcomes(parents[j]);
				//System.out.println("Outcome index = "+((i/modulesArray[j])%parentsDim[j]));
				String outcomeToAdd = temporalParentOutcomes[(i/modulesArray[j])%parentsDim[j]];
				parentsConfig.add(new Parent(parents[j],outcomeToAdd));
			}

			/*System.out.println("Parent configuration:");
			for(Parent parentToPrint:parentsConfig){
				System.out.println("Parent: "+parentToPrint.toString());
			}*/

			//A questo punto in parentsConfig c'è la lista di tutti gli outcome dei padri relativi all'outcome
			//i%outcomes.length del nodo
			String actualOutcome = outcomes[i%outcomes.length];
			List<Observation> observationsToParse = getObservationsByConfiguration(parentsConfig,actualOutcome,filteredObservations);

			/*System.out.println("Retrieved observations:");
			for(Observation toPrint:observationsToParse){
				System.out.println("Retrieved observation: "+toPrint.toString());
			}*/

			if(observationsToParse.size()==0){
				//Non va proiettato
				projectionList.add(prob[i]);
			}else{

				double numerator = 0.0;
				double denominator = 0.0;

				for(Observation obs:observationsToParse){
					numerator += Math.pow(1.0*(obs.getTimestamp()/t), gamma);
					//System.out.println("Partial numerator: "+numerator);
					denominator += Math.pow((obs.getTimestamp()/t), gamma);
				}

				double oldProbability = 0.0;
				double oldTimeStamp = bn.getLastUpdate(); 
				oldProbability = prob[i];

				numerator += Math.pow(oldProbability*(oldTimeStamp/t), gamma);
				denominator += Math.pow((oldTimeStamp/t), gamma);
				
				double projection = numerator/denominator;
				//Normalizzazione!
				projectionList.add(projection);
				//System.out.println("Projection, node:"+node+" outcome: "+outcomes[j]+" value:"+projection);
			}

		}

		//Manca l'ultimo gruppo da normalizzare
		if(prob.length>0){
			double alpha = 0;
			for(double proj:projectionList){
				alpha+=proj;
			}
			for(double proj:projectionList){
				double toAdd = proj/alpha;
				newProbs.add(toAdd);
				//System.out.println("New prob, node:"+node+" value:"+toAdd);
			}
			projectionList.clear(); //Ora sono aggiunti, si deve ripartire
		}

		//System.out.println("New probs dim: "+newProbs.size());
		//Settagio nuovo vettore di probabilità
		double[] probsToSet = new double[newProbs.size()];
		for(int i=0;i<newProbs.size();i++){
			probsToSet[i] = newProbs.get(i);
		}
		try {
			bn.getWrapper().setProbabilites(node, probsToSet);
			bn.setLastUpdate((long) t);
		} catch (JayesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean contains(List<Parent> parents, String nodeName, String outcome){
		for(Parent p:parents){
			if(p.getNodeName().equals(nodeName) && p.getOutcome().equals(outcome))
				return true;
		}
		return false;
	}

	private List<Observation> getObservationsByConfiguration(List<Parent> parents, String outcome, List<Observation> observationList){
		List<Observation> toReturn  = new ArrayList<Observation>();
		for(Observation obs:observationList){
			if(obs.getObservedOutcome().equals(outcome)){
				List<ConditionalNode> conditionalNodes = obs.getNodeNameCond();
				boolean toAdd = true;
				for(ConditionalNode cn:conditionalNodes){
					if(!contains(parents,cn.getNodeName(),cn.getOutcome())){
						toAdd=false;
						break; //Trovato uno non contenuto
					}		
				}
				if(toAdd)
					toReturn.add(obs);
			}
		}

		return toReturn;
	}
	
	public void updateNoParentsNodeMaximumLikelihood(List<Observation> observationList,
			String node){
		
		Wrapper wrapper = bn.getWrapper();
		
		//Cerco tutte le nuove osservazioni legate al nodo
		List<Observation> filteredObservations = getObservationsByNodeName(node,observationList);
		
		String [] outcomes = wrapper.getOutcomes(node);
		
		double [] likelihoods = new double [outcomes.length];
		
		//Likelihoods computations
		int count = 0;
		for(int i=0; i<outcomes.length; i++){
			count = 0;
			for(Observation obs:filteredObservations){
				if(obs.getObservedOutcome().equalsIgnoreCase(outcomes[i])){
					count++;
				}
			}
			likelihoods[i] = (double) count/filteredObservations.size();
		}
		
		NodeProbability nodeProbability = null;
		//Ottenimento di nodeprobability
		try {
			//System.out.println("Node: "+node);
			nodeProbability = wrapper.getNodeProbability(node);
		} catch (JayesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		List<Double> newProbs = new ArrayList<Double>();
		for(int i=0; i<outcomes.length; i++){
			double oldP;
			try {
				oldP = nodeProbability.getPriorProbabilityByOutcome(outcomes[i]);
			} catch (ProbabilityNotFoundException e) {
				oldP = 0.0;
				e.printStackTrace();
			}
			newProbs.add((likelihoods[i]+oldP)/2);
			//newProbs.add(likelihoods[i]);
		}
		
		double probsToSet[] = new double[newProbs.size()];
		for(int i=0; i<newProbs.size();i++){
			probsToSet[i] = newProbs.get(i);
		}
		try {
			bn.getWrapper().setProbabilites(node, probsToSet);
		} catch (JayesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	public void updateNoParentsNode(List<Observation> observationList,
			String node, double t, double gamma){

		//Costruzione struttura rete attuale
		Wrapper wrapper = bn.getWrapper();

		NodeProbability nodeProbability = null;
		//Ottenimento di nodeprobability
		try {
			//System.out.println("Node: "+node);
			nodeProbability = wrapper.getNodeProbability(node);
		} catch (JayesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		//Cerco tutte le nuove osservazioni legate al nodo
		List<Observation> filteredObservations = getObservationsByNodeName(node,observationList);
		System.out.println("Totale osservazioni: "+filteredObservations.size());
		//Bisogna recuperare la lista degli outcome (si aggiornano i singoli valori)
		String [] outcomes = bn.getWrapper().getOutcomes(node);
		double[] oldProbs = null;
		try {
			oldProbs = bn.getWrapper().getProbabilities(node);
		} catch (JayesException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		//Per ogni outcomes(sto considerando solo tabelle a priori)
		List<Double> projectionList = new ArrayList<Double>();
		for(int j=0; j<outcomes.length;j++){
			//Cerco osservazioni che abbiano solo quello specifico outcome
			List<Observation> observationsToParse = getObservationByOutcome(outcomes[j],filteredObservations);
			System.out.println("Outcome "+outcomes[j]+" osservazioni: "+observationsToParse.size());
			if(observationsToParse.size()==0){
				//Non va proiettato
				projectionList.add(oldProbs[j]);
			}else{
				double prob = (double) observationsToParse.size()/filteredObservations.size();
				double numerator = 0.0;
				double denominator = 0.0;

				for(Observation obs:observationsToParse){
					numerator += Math.pow(prob*(obs.getTimestamp()/t), gamma); //!!!!!
					//System.out.println("Partial numerator: "+numerator);
					denominator += Math.pow((obs.getTimestamp()/t), gamma);
				}

				double oldProbability = 0.0;
				double oldTimeStamp = bn.getLastUpdate();
				try {
					oldProbability = nodeProbability.getPriorProbabilityByOutcome(outcomes[j]);
				} catch (ProbabilityNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				numerator += Math.pow(oldProbability*(oldTimeStamp/t), gamma);
				denominator += Math.pow((oldTimeStamp/t), gamma);

				double projection = numerator/denominator;
				//Normalizzazione!
				projectionList.add(projection);
				//System.out.println("Projection, node:"+node+" outcome: "+outcomes[j]+" value:"+projection);
			}

		}
		//Normalizzazione:
		double alpha = 0;
		for(double proj:projectionList){
			alpha+=proj;
		}

		List<Double> newProbs = new ArrayList<Double>();
		for(double proj:projectionList){
			double toAdd = proj/alpha;
			newProbs.add(toAdd);
			//System.out.println("New prob, node:"+node+" value:"+toAdd);
		}
		double probsToSet[] = new double[newProbs.size()];
		for(int i=0; i<newProbs.size();i++){
			probsToSet[i] = newProbs.get(i);
		}
		try {
			bn.getWrapper().setProbabilites(node, probsToSet);
			bn.setLastUpdate((long) t);
		} catch (JayesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		double [] probs = null;
		try {
			probs = bn.getWrapper().getProbabilities(node);
		} catch (JayesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Nuove probabilità di "+node);
		String [] outcomesFinal = bn.getWrapper().getOutcomes(node);
		for(int i=0; i<probs.length;i++){
			System.out.println("Prob "+outcomesFinal[i]+" : "+probs[i]);
		}

	}

	private List<Observation> getObservationsByNodeName(String nodeName, List<Observation> observations){
		List<Observation> toReturn  = new ArrayList<Observation>();
		for(Observation o:observations){
			if(o.getNodeName().equals(nodeName))
				toReturn.add(o);
		}
		return toReturn;
	}

	private List<Observation> getObservationByOutcome(String outcome, List<Observation> observations){
		List<Observation> toReturn  = new ArrayList<Observation>();
		for(Observation o:observations){
			if(o.getObservedOutcome().equals(outcome))
				toReturn.add(o);
		}
		return toReturn;
	}
}


