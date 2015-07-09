package simulatore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import Wrapper.ComposedProbability;
import Wrapper.JayesException;
import Wrapper.NodeProbability;
import Wrapper.Wrapper;
import bayesANDjess.BayesNet;
import xml_handler.NetUpdater;
import xml_handler.Observation;

public class Avvio {
	private BayesNet likelihoodNet;
	private List<BayesNet> bnList;
	private GregorianCalendar calendar;
	private List<Observation> observationList;
	private List<PrinterStructure> printerList;
	private static double baseSeed = 0.8;
	/*
	 * 		noParentsNodes.add("temperatura");
		noParentsNodes.add("condizioni_meteo");
		noParentsNodes.add("mezzo");
		noParentsNodes.add("km_to_run");
		noParentsNodes.add("momento_viaggio");
	 */
	
	public Avvio(int gammasNumber){
		calendar = new GregorianCalendar();
		observationList = new ArrayList<Observation>();
		
		printerList = new ArrayList<PrinterStructure>();

		
		bnList = new ArrayList<BayesNet>();
		
		for(int i=0;i<gammasNumber;i++){
			bnList.add(new BayesNet());
		}
		
		this.likelihoodNet = new BayesNet();
		
		Wrapper wrapper = bnList.get(0).getWrapper();
		String [] nodes = wrapper.getNodes();
		
		for(String node:nodes){
			try {
				printerList.add(new PrinterStructure(wrapper.getNodeProbability(node)));
			} catch (JayesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	
	/*public Avvio(){
		bn = new BayesNet();
		calendar = new GregorianCalendar();
		observationList = new ArrayList<Observation>();
		
		printerList = new ArrayList<PrinterStructure>();
		Wrapper wrapper = bn.getWrapper();
		String [] nodes = wrapper.getNodes();
		
		for(String node:nodes){
			try {
				printerList.add(new PrinterStructure(wrapper.getNodeProbability(node)));
			} catch (JayesException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}*/
	
	private void print(NodeProbability np, NodeProbability npLikelihood, double gamma, int runNumber, List<Observation> observationList){
		PrinterStructure printerStructure = null;
		for(PrinterStructure ps:printerList){
			if(ps.getNp().getNode().equals(np.getNode())){
				printerStructure = ps;
			}
		}
		
		List<ComposedProbability> cpList = np.getProbabilities();
		List<ComposedProbability> cpListLikelihood = npLikelihood.getProbabilities();
		PrintWriter pw = printerStructure.getPw();
		String line = runNumber+","+gamma;
		int count = 0;		
		for(int i = 0; i<cpList.size(); i++){
			for(Observation obs:observationList){
				if(obs.getNodeName().equals(np.getNode()) && obs.getObservedOutcome().equals(cpList.get(i).getNodeOutcome())){
					count++;
				}
			}
			if(count>0)
				line += ","+cpList.get(i).getProbability()+","+count+","+cpListLikelihood.get(i).getProbability();
			else
				line += ","+cpList.get(i).getProbability()+","+0+","+cpListLikelihood.get(i).getProbability();
			
			count = 0;
		}
		
		pw.println(line);
	}
	
	private double[] generateConditionalSeeds(int size, double startingSeed, int run, int runsToDo){
		double seeds[] = new double[size];
		
		//Scelta del startingSeed
		double d_outcome = (double) run/runsToDo;
		double seed_index_par = d_outcome*10;
		
		
		int seed_index = ((int) seed_index_par)%size;
		
		System.out.println("Run: "+run+"Par: "+seed_index_par+" seed_index: "+seed_index);
		seeds[seed_index] = startingSeed;
		
		double remain = (1.0-startingSeed)/(seeds.length-1);
		
		for(int i=0; i<seeds.length; i++){
			if(i!=seed_index)
				seeds[i] = remain;
		}
		
		return seeds;
	}
	
	private double[] generateRandomSeeds(int size){
		double seeds[] = new double[size];
		for(int i=0; i<seeds.length; i++){
			seeds[i] = Math.random();
		}
		
		double sum = 0;
		for(double seed:seeds){
			sum += seed;
		}
		
		for(int i=0; i<seeds.length;i++){
			seeds[i] = seeds[i]/sum;
		}
		
		return seeds;
	}
	
	public void singleRun(List<String> noParentsNodes, int maxObservations, List<Double> gammas, int runNumber, int runsToDo){
		//Quanti nodi voglio aggiornare
				//double d_nodes = Math.random();
				int desiredNodes =noParentsNodes.size();
				//desiredNodes++; //Non arriva mai a 5
				
				//Lista nodi da aggiornare
				List<String> nodesToUpdate = new ArrayList<String>();
				
				int max_observations = 100;
				
				while(desiredNodes>0){
					//Prendo i primi desired_nodes della lista, non li tiro a caso
					Wrapper wrapper =bnList.get(0).getWrapper();
					
					String outcomes[] = wrapper.getOutcomes(noParentsNodes.get(desiredNodes-1)); //Evito outofbound
					nodesToUpdate.add(noParentsNodes.get(desiredNodes-1));
					
					//double seeds [] = generateRandomSeeds(outcomes.length);
					double seeds[] = generateConditionalSeeds(outcomes.length,baseSeed,runNumber,runsToDo);
					for(int i =0; i<outcomes.length; i++){
						int number_of_observations = (int) (seeds[i]*max_observations);
						
						while(number_of_observations > 0){
							Observation obs = new Observation();
							obs.setNodeName(noParentsNodes.get(desiredNodes-1));
							obs.setObservedOutcome(outcomes[i]);
							obs.setTimestamp(calendar.getTimeInMillis()+(10000*number_of_observations)); //Introduco della variabilità

							observationList.add(obs);

							number_of_observations--;
						}

						
					}
					desiredNodes--;
					/*
					//Calcolo di quale outcome genero 
					double d_outcome = Math.random();
					int outcome_index = (int) (d_outcome*outcomes.length);

					System.out.println("Scelto outcome: "+outcomes[outcome_index]);

					//Calcolo del numero di osservazioni da generare
					double d_observations = Math.random();
					int number_of_observations = (int) (d_observations*max_observations);

					while(number_of_observations > 0){
						Observation obs = new Observation();
						obs.setNodeName(noParentsNodes.get(desiredNodes-1));
						obs.setObservedOutcome(outcomes[outcome_index]);
						obs.setTimestamp(calendar.getTimeInMillis()+(10000*number_of_observations)); //Introduco della variabilità

						observationList.add(obs);

						number_of_observations--;
					}

					desiredNodes--;*/
				}

				/*for(Observation o: observationList){
					System.out.println("Osservazione: "+o.toString());
				}*/
				
				NetUpdater netUpdaterLikelihood = new NetUpdater(likelihoodNet);
				for(String node:nodesToUpdate){
					netUpdaterLikelihood.updateNoParentsNodeMaximumLikelihood(observationList, node);
				}
				
				for(int i=0; i<bnList.size();i++){
					NetUpdater netUpdater = new NetUpdater(bnList.get(i));
					
					long startTime = calendar.getTimeInMillis();

					for(String node:nodesToUpdate){
						System.out.println("Observations list size: "+observationList.size());
							netUpdater.updateNoParentsNode(observationList, node, calendar.getTimeInMillis()+(10000*(max_observations+1)), gammas.get(i));
							//Il timestamp nella stampa serve per indicare l'andamento nei vari "run"
							try {
								NodeProbability np = bnList.get(i).getWrapper().getNodeProbability(node);
								NodeProbability npLikelihood = likelihoodNet.getWrapper().getNodeProbability(node);
								print(np,npLikelihood,gammas.get(i),runNumber,observationList);

							} catch (JayesException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("Aggiornato "+node+" in: "+(calendar.getTimeInMillis()-startTime)+"ms");
					}
				}
				 
				
				/*NetUpdater netUpdater = new NetUpdater(bn);
				long startTime = calendar.getTimeInMillis();

				for(String node:nodesToUpdate){
					System.out.println("Observations list size: "+observationList.size());
					for(Double gamma:gammas){
						netUpdater.updateNoParentsNode(observationList, node, calendar.getTimeInMillis()+(10000*(max_observations+1)), gamma);
						//Il timestamp nella stampa serve per indicare l'andamento nei vari "run"
						try {
							NodeProbability np = bn.getWrapper().getNodeProbability(node);
							print(np,gamma,runNumber,observationList);

						} catch (JayesException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.out.println("Aggiornato "+node+" in: "+(calendar.getTimeInMillis()-startTime)+"ms");
					}
				}*/



				observationList.clear();
	}
	
	public static void main(String[] args) {
		/*BayesNet bn = new BayesNet();
		GregorianCalendar calendar = new GregorianCalendar();
		List<Observation> observationList = new ArrayList<Observation>();
		*/
		
		List<String> noParentsNodes = new ArrayList<String>();
		/*noParentsNodes.add("temperatura");
		noParentsNodes.add("condizioni_meteo");
		noParentsNodes.add("mezzo");
		noParentsNodes.add("km_to_run");
		noParentsNodes.add("momento_viaggio");*/
		noParentsNodes.add("Queue");
		noParentsNodes.add("Agent_Type");
		
		List<Double> gammas = new ArrayList<Double>();
		gammas.add(1.0);
		gammas.add(0.1);
		//gammas.add(0.5);
		//gammas.add(2.0);
		//gammas.add(4.0);
		
		Avvio avvio = new Avvio(gammas.size());
		
		int runsToDo=100,runs=0;
		while(runs<runsToDo){
			avvio.singleRun(noParentsNodes, 100, gammas,runs+1,runsToDo);
			runs++;
		}
		
		List<PrinterStructure> printers = avvio.printerList;
		
		for(PrinterStructure p:printers){
			p.closeStream();
		}
		
		/*
		//Quanti nodi voglio aggiornare
		double d_nodes = Math.random();
		int desiredNodes = (int) (d_nodes*noParentsNodes.size());
		desiredNodes++; //Non arriva mai a 5
		
		//Lista nodi da aggiornare
		List<String> nodesToUpdate = new ArrayList<String>();
		
		int max_observations = 100;
		
		while(desiredNodes>0){
			//Prendo i primi desired_nodes della lista, non li tiro a caso
			Wrapper wrapper =bn.getWrapper();
			
			String outcomes[] = wrapper.getOutcomes(noParentsNodes.get(desiredNodes-1)); //Evito outofbound
			nodesToUpdate.add(noParentsNodes.get(desiredNodes-1));
			
			//Calcolo di quale outcome genero 
			double d_outcome = Math.random();
			int outcome_index = (int) (d_outcome*outcomes.length);
			
			System.out.println("Scelto outcome: "+outcomes[outcome_index]);
			
			//Calcolo del numero di osservazioni da generare
			double d_observations = Math.random();
			int number_of_observations = (int) (d_observations*max_observations);
			
			while(number_of_observations > 0){
				Observation obs = new Observation();
				obs.setNodeName(noParentsNodes.get(desiredNodes-1));
				obs.setObservedOutcome(outcomes[outcome_index]);
				obs.setTimestamp(calendar.getTimeInMillis()+(10000*number_of_observations)); //Introduco della variabilità
				
				observationList.add(obs);
				
				number_of_observations--;
			}
			
			desiredNodes--;
		}
		
		/*for(Observation o: observationList){
			System.out.println("Osservazione: "+o.toString());
		}*/
		
		/*NetUpdater netUpdater = new NetUpdater(bn);
		long startTime = calendar.getTimeInMillis();
		
		for(String node:nodesToUpdate){
			System.out.println("Observations list size: "+observationList.size());
			netUpdater.updateNoParentsNode(observationList, node, calendar.getTimeInMillis()+(10000*(max_observations+1)), 100000);
			System.out.println("Aggiornato "+node+" in: "+(calendar.getTimeInMillis()-startTime)+"ms");
		}*/
		
	}

}
