package Wrapper;

import java.util.List;

public class ComposedProbability {
	private List<String> parentOutcomes;
	private String[] parents;
	private String nodeOutcome;
	private double probability;
	
	public ComposedProbability(List<String> parentOutcomes, String[] parents,
			String nodeOutcome, double probability) {
		super();
		this.parentOutcomes = parentOutcomes;
		this.parents = parents;
		this.nodeOutcome = nodeOutcome;
		this.probability = probability;
	}
	
	public ComposedProbability(String nodeOutcome, double probability){
		this.parentOutcomes = null;
		this.parents = null;
		this.nodeOutcome = nodeOutcome;
		this.probability = probability;
	}
	
	public List<String> getParentOutcomes() {
		return parentOutcomes;
	}

	public String[] getParents() {
		return parents;
	}

	public String getNodeOutcome() {
		return nodeOutcome;
	}

	public double getProbability() {
		return probability;
	}
	
	public void setProbability(double probability) {
		this.probability = probability;
	}

	@Override
	public String toString() {
		String toReturn = "ComposedProbability [";
		for(int i=0; i<parents.length; i++){
			toReturn = toReturn+"parentsNode="+parents[i]+", parentsOutcome="+parentOutcomes.get(i)+", ";
		}
		toReturn = toReturn+"nodeOutcome=" + nodeOutcome + ", probability="
				+ probability + "]";
		return toReturn;
	}
	
	
}
