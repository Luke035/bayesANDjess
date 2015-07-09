package Wrapper;

import java.util.Map;

import org.eclipse.recommenders.jayes.BayesNode;

public class Results {
	private final String node;
	private final String[] outcomes;
	private final String[] evidences;
	private final double[] values;
	private final double[] weightedValues;

	public Results(String node, Map<BayesNode, String> evidences, 
			String[] outcomes, double[] values){
		this.node = node;
		this.evidences = new String[evidences.size()];
		int i = 0;
		for (BayesNode n:evidences.keySet()){
			this.evidences[i] = n.getName() + " = ";
			this.evidences[i] += evidences.get(n);
			i++;
		}
		String[] outcomes2 = new String[outcomes.length];
		double[] values2 = new double[values.length];

		int[] order = orderOutcomes(outcomes, values);

		for (i=0; i<order.length; i++){
			outcomes2[order[i]] = outcomes[i];
			values2[order[i]] = values[i];
		}
		this.outcomes = outcomes2;
		this.values = values2;
		this.weightedValues = weightValues(values2);
	}

	private int[] orderOutcomes(String[] outcomes, double[] values){
		int[] order = new int[values.length];
		for (int i = 0; i<values.length-1; i++){
			for (int j = i+1; j<values.length; j++){
				if (values[i]<values[j]){
					order[i]++;
				} else {
					order[j]++;
				}
			}
		}
		return order;
	}

	private double[] weightValues(double[] values){
		double max = 0;
		for (double v:values){
			if (max<v) {
				max = v;
			}
		}
		double wv[] = values.clone();
		for (int i = 0; i<wv.length; i++){
			wv[i] /= max;
		}
		return wv;
	}

	public String getNode() {
		return node;
	}

	public String[] getEvidences() {
		return evidences;
	}

	public String[] getOutcomes() {
		return outcomes;
	}

	public double[] getValues() {
		return values;
	}

	public double[] getWeightedValues() {
		return weightedValues;
	}

	public String getQuery(){
		String tS = "P(" + node;
		if (evidences.length>0){
			tS += " | " + evidences[0];
			for (int i=1; i<evidences.length; i++){
				tS += ", " + evidences[i];
			}
		}
		tS += ")";
		return tS;
	}
	
	public String getResults(){
		String tS = "<" + outcomes[0] + "=" + String.format("%1$.3f", weightedValues[0]);
		for (int i=1; i<outcomes.length; i++){
			tS += "; " + outcomes[i] + "=" + String.format("%1$.3f", weightedValues[i]);
		}
		tS += ">";
		return tS;
	}
	
	public String getResultsNormal(){
		String tS = "<" + outcomes[0] + "=" + String.format("%1$.3f", values[0]);
		for (int i=1; i<outcomes.length; i++){
			tS += "; " + outcomes[i] + "=" + String.format("%1$.3f", values[i]);
		}
		tS += ">";
		return tS;
	}
	
	public String toString() {
		String tS = getQuery();
		tS += " = ";
		if (values.length >2)
			tS += getResults();
		else
			tS += getResultsNormal();
		return  tS;
	}

}
