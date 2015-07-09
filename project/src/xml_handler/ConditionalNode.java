package xml_handler;

public class ConditionalNode {
	private String nodeName;
	private String outcome;
	
	public String getNodeName() {
		return nodeName;
	}
	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}
	public String getOutcome() {
		return outcome;
	}
	public void setOutcome(String outcome) {
		this.outcome = outcome;
	}
	
	@Override
	public String toString() {
		return "ConditionalNode [nodeName=" + nodeName + ", outcome=" + outcome
				+ "]";
	}
}
