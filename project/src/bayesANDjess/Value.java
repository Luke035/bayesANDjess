package bayesANDjess;

public class Value {
	private String outcome;
	private double probability;
	
	public Value(String outcome, double probability) {
		super();
		this.outcome = outcome;
		this.probability = probability;
	}

	public String getOutcome() {
		return outcome;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}
	
	
}
