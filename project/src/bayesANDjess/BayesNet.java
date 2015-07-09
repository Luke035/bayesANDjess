package bayesANDjess;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

import Wrapper.InfAlgorithm;
import Wrapper.JayesException;
import Wrapper.NetLoader;
import Wrapper.Wrapper;


public class BayesNet {
	public static String netPath = "./Dati_bayesian_net_agent.csv";
	//public static String netPath = "./Musictext_net.csv";
	private Wrapper wrapper;
	private ArrayList<Evidence> evidencesConfigurations;
	private long lastUpdate;
	public BayesNet(){
		NetLoader nl = new NetLoader();
		nl.buildNet(netPath);
		this.wrapper = nl.getNet();
		
		GregorianCalendar calendar = new GregorianCalendar();
		lastUpdate = calendar.getTimeInMillis();
	}
	
	public long getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(long lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public Wrapper getWrapper() {
		return wrapper;
	}

	public ArrayList<String> getNodesToInfer(ArrayList<String> nodeEvidence){
		ArrayList<String> nodesToInfer = new ArrayList<String>(Arrays.asList(wrapper.getNodes()));//Inizialmente tutti	
		for(int i=0; i<nodeEvidence.size(); i++){
			if(nodesToInfer.contains(nodeEvidence.get(i))){
				nodesToInfer.remove(nodeEvidence.get(i));
			}
		}
		
		return nodesToInfer;
	}
	
	/*
	 * Ritorna la lista di tutti gli outcome dei nodo da sottoporre a evidenza 
	 */
	public ArrayList<Result> inferEnumerate(ArrayList<String> nodeEvidence){
		//Recupero nodi da inferire
		ArrayList<String> nodesToInfer = new ArrayList<String>(Arrays.asList(wrapper.getNodes()));//Inizialmente tutti	
		for(int i=0; i<nodeEvidence.size(); i++){
			if(nodesToInfer.contains(nodeEvidence.get(i))){
				nodesToInfer.remove(nodeEvidence.get(i));
			}
		}
		ArrayList<Evidence> evidenceConfigurations = constructEvidenceConfigurations(nodeEvidence);
		this.evidencesConfigurations = evidenceConfigurations;
		/*
		 * A ogni nodo NON di evidenza viene applicata la procedura di infrenza una
		 * volta per ogni configurazione di evidenza presente
		 */
		ArrayList<Result> toReturn = new ArrayList<Result>();
		for(int i=0; i<nodesToInfer.size(); i++){ //Ogni nodo non di evidenza
			for(int j=0; j<evidenceConfigurations.size(); j++){
				try {
					wrapper.inference(nodesToInfer.get(i), 
							evidenceConfigurations.get(j).parseEvidence(), InfAlgorithm.EXACT);
				} catch (JayesException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				toReturn.add(new Result(nodesToInfer.get(i),
						wrapper.getResults().getValues(),
						wrapper.getResults().getOutcomes(),
						evidenceConfigurations.get(j)));
			}
		}
		
		printInferenceResult(toReturn);
		//return constructEvidenceConfigurations(nodeEvidence);
		return toReturn;
	}
	
	
	private void printInferenceResult(List<Result> list){
		for(int i=0; i<list.size(); i++){
			list.get(i).printResult();
		}
	}
	
	//Metodo per ottenere la lista di tutte le configurazioni di evidenze possibili
	public ArrayList<Evidence> constructEvidenceConfigurations(ArrayList<String> nodesToInfer){
		ArrayList<Evidence> evidenceList = new ArrayList<Evidence>();
		int i=0;
		while(i<nodesToInfer.size()){
			String outcomes[] = wrapper.getOutcomes(nodesToInfer.get(i));
			if(evidenceList.size() == 0){ //Caso base
				//Ogni outcome una evidenza
				for(int j=0; j<outcomes.length; j++){
					Evidence e = new Evidence();
					e.addEvidence(nodesToInfer.get(i), outcomes[j]);
					evidenceList.add(e);
				}
			}else{
				ArrayList<Evidence> newEvidenceList = new ArrayList<Evidence>();
				for(int j=0; j<outcomes.length; j++){
					for(int k=0; k<evidenceList.size(); k++){
						//Aggiungere evidenza vecchia in evidenceList con nuovo outcome in j
						Evidence e = new Evidence();
						e.insertOtherEvidence(evidenceList.get(k));
						e.addEvidence(nodesToInfer.get(i), outcomes[j]);
						newEvidenceList.add(e);
					}
				}
				evidenceList = newEvidenceList; //Aggiornamento lista di evidenze
			}
			i++;
		}
		return evidenceList;
	}
	
	public ArrayList<Evidence> getEvidencesConfigurations(){
		if(this.evidencesConfigurations != null)
			return this.evidencesConfigurations;
		else
			return null; //TODO throw exception
	}
	
	public static void main(String [] args){
		BayesNet bn = new BayesNet();
		/*ArrayList<String> evidence = new ArrayList<String>();
		evidence.add("Coda");
		evidence.add("Tipologia");
		//evidence.add("Disponbilita a coda");
		//evidence.add("Mezzo");
		/*ArrayList<Evidence> result = bn.inferEnumerate(evidence);
		for(int i=0; i<result.size(); i++){
			System.out.println("Configurazione "+(i+1));
			result.get(i).printEvidence();
			System.out.println("-----------------------");
		}*/
		/*ArrayList<Result> toPrint = bn.inferEnumerate(evidence);
		for(int i=0; i<toPrint.size(); i++){
			System.out.println((i+1)+" Nodo "+toPrint.get(i).getNode());
			toPrint.get(i).printResult();
		}*/
	}
}
