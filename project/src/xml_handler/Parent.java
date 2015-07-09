package xml_handler;

import java.util.List;

public class Parent {
	private String nodeName;
	private String outcome;
	
	public Parent(String nodeName, String outcomes) {
		super();
		this.nodeName = nodeName;
		this.outcome = outcomes;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getOutcome() {
		return outcome;
	}

	@Override
	public String toString() {
		return "Parent [nodeName=" + nodeName + ", outcome=" + outcome + "]";
	}
	
}
