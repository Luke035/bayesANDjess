package rule_from_csv;

import java.util.List;

public class SimpleRule {
	private List<String> lhs;
	private String rhs;
	private String lhsTitle;
	private String rhsTitle;
	
	public SimpleRule(List<String> lhs, String rhs, String lhsTitle, String rhsTitle){
		this.lhs = lhs;
		this.rhs = rhs;
		this.lhsTitle = lhsTitle;
		this.rhsTitle = rhsTitle;
	}
	
	public String getLhsTitle() {
		return lhsTitle;
	}

	public void setLhsTitle(String lhsTitle) {
		this.lhsTitle = lhsTitle;
	}

	public String getRhsTitle() {
		return rhsTitle;
	}

	public void setRhsTitle(String rhsTitle) {
		this.rhsTitle = rhsTitle;
	}

	public List<String> getLhs() {
		return lhs;
	}

	public void setLhs(List<String> lhs) {
		this.lhs = lhs;
	}

	public String getRhs() {
		return rhs;
	}

	public void setRhs(String rhs) {
		this.rhs = rhs;
	}

	/*@Override
	public String toString() {
		return "SimpleRule [lhs=" + lhs + ", rhs=" + rhs + "]";
	}*/
	
	public String generateJESSRule(int ruleIndex){
		String jess_command ="(defrule ArtistToArtistRule"+ruleIndex+"\n";
		String lhsToPrint = "("+this.lhsTitle; //To parametrizing
		for(int i=0; i<lhs.size();i++){
			lhsToPrint = lhsToPrint+" "+checkText(lhs.get(i),' ','-');
		}
		lhsToPrint = lhsToPrint+")";
		
		jess_command = jess_command+" 	"+lhsToPrint+"\n";
		jess_command = jess_command+" 	=>\n";
		jess_command = jess_command+"	(assert ("+this.rhsTitle+" "+checkText(rhs,' ','-')+")))";
		//jess_command = jess_command+" 	(assert ("+this.rhsTitle+" "+checkText(rhs,' ','-')+")))";
		return jess_command;
	}
	
	private String checkText(String toCheck, char toReplace, char substitute){
		return toCheck.replace(toReplace, substitute);
	}
}
