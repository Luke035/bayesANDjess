package bayesANDjess;
import java.io.PrintStream;
import java.util.ArrayList;


public class Rule {
	private Evidence lhs;
	private String nodeRHS;
	private String outcomeRHS;
	private double reliability;
	private int indexNumber;
	
	public Rule(Evidence lhs, String nodeRHS, String outcomeRHS, double probability, int indexNumber) {
		super();
		this.lhs = lhs;
		this.nodeRHS = nodeRHS;
		this.outcomeRHS = outcomeRHS;
		this.reliability = probability;
		this.indexNumber = indexNumber;
	}
	
	private String checkBlankSpace(String ruleName){
		char [] stringArray = ruleName.toCharArray();
		String toReturn="";
		for(int i=0; i<stringArray.length; i++){
			if(stringArray[i]==' ')
				toReturn  = toReturn+"_";
			else
				toReturn = toReturn+stringArray[i];
		}
		return toReturn;
	}
	
	public void addRuleInJess(JessEngine jessEngine){
		String ruleName = nodeRHS.toUpperCase()+outcomeRHS;
		ruleName = checkBlankSpace(ruleName);
		ruleName = ruleName+indexNumber;
		String lhsString = "";
		ArrayList<String> lhsNodes = lhs.getNodes();
		ArrayList<String> lhsOutcome = lhs.getOutcomes();
		for(int i=0; i<lhsNodes.size(); i++){
			lhsString = lhsString+"("+checkBlankSpace(lhsNodes.get(i))+" "+checkBlankSpace(lhsOutcome.get(i))+")  ";
		}
		String rhsString = "(assert (Result_"+checkBlankSpace(nodeRHS)+" ("+checkBlankSpace(nodeRHS)+" "+checkBlankSpace(outcomeRHS)+
				") (Reliability "+this.reliability+"))))";
		jessEngine.addRule(ruleName, lhsString, rhsString);
	}
	
	public void printRule(PrintStream printer){
		String ruleName = nodeRHS.toUpperCase()+outcomeRHS;
		ruleName = checkBlankSpace(ruleName);
		ruleName = ruleName+indexNumber;
		printer.println("(defrule "+ruleName);
		String lhsString = "	";
		ArrayList<String> lhsNodes = lhs.getNodes();
		ArrayList<String> lhsOutcome = lhs.getOutcomes();
		for(int i=0; i<lhsNodes.size(); i++){
			lhsString = lhsString+"("+checkBlankSpace(lhsNodes.get(i))+" "+checkBlankSpace(lhsOutcome.get(i))+")  ";
		}
		printer.println(lhsString); //Print LHS rule
		printer.println("=>");
		String rhsString = "(assert (Result_"+checkBlankSpace(nodeRHS)+" ("+checkBlankSpace(nodeRHS)+" "+checkBlankSpace(outcomeRHS)+
				") (Reliability "+this.reliability+"))))";
		printer.println(rhsString); //Print RHS rule
		printer.println();
	}
}
