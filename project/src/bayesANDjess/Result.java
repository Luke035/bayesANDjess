package bayesANDjess;

public class Result {
	private String node;
	private Value[] values;
	private Evidence evidence;
	
	public Result(String node, double[] results, String [] outcomes, Evidence evidence) {
		super();
		this.node = node;
		this.values = new Value[results.length];
		for(int i=0; i<results.length; i++){
			values[i] = new Value(outcomes[i], results[i]);
		}
		this.evidence = evidence;
	}

	public Result(String node, Value[] values, Evidence evidence) {
		super();
		this.node = node;
		this.values = values;
		this.evidence = evidence;
	}

	public String getNode() {
		return node;
	}

	public Value[] getValues() {
		return values;
	}
	
	public Evidence getEvidence(){
		return evidence;
	}
	
	public Value getMostProbableValue(){
		//The most probable value is the first in Values array
		return this.values[0];
	}
	
	public void printResult(){
		System.out.println("Node: "+this.node);
		System.out.println("Configurazione corrispondente:");
		evidence.printEvidence();
		for(int i=0; i<values.length; i++){
			System.out.println("--------- Outcome: "+values[i].getOutcome()+
					" Probabilities: "+values[i].getProbability());
		}
	}
}
