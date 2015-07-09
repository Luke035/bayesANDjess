package bayesANDjess;
import java.util.ArrayList;


public class Evidence {
	private ArrayList<String> nodes;
	private ArrayList<String> outcomes;
	
	public Evidence(){
		nodes = new ArrayList<String>();
		outcomes = new ArrayList<String>();
	}
	
	public void insertOtherEvidence(Evidence e){
		if(e.getNodes().size() != e.getOutcomes().size()){
			System.err.println("Uncorrect evidence");
			return; //Some error
		}
		for(int i=0; i<e.getNodes().size();i++){
			nodes.add(e.getNodes().get(i));
			outcomes.add(e.getOutcomes().get(i));
		}
	}
	
	public void addEvidence(String node, String outcome){
		nodes.add(node);
		outcomes.add(outcome);
	}
	
	public String[][] parseEvidence(){
		String [][] parsedEvidences = new String[2][nodes.size()];
		for(int i=0; i<nodes.size(); i++){
			parsedEvidences[0][i] = nodes.get(i);
			parsedEvidences[1][i] = outcomes.get(i);
		}
		
		return parsedEvidences;
	}

	public ArrayList<String> getNodes() {
		return nodes;
	}

	public ArrayList<String> getOutcomes() {
		return outcomes;
	}
	
	public void printEvidence(){
		for(int i=0; i<nodes.size(); i++){
			System.out.println("...... Nodo: "+nodes.get(i)+" Outcome: "+outcomes.get(i));
		}
	}
}
