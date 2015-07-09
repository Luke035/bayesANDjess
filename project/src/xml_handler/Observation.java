package xml_handler;

import java.util.ArrayList;
import java.util.List;

public class Observation {
	private String nodeName;
	private String observedOutcome;
	private List<ConditionalNode> nodeNameCond;
	private long timestamp;
	
	public Observation(){
		nodeNameCond = new ArrayList<ConditionalNode>();
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public void setNodeNameCond(List<ConditionalNode> nodeNameCond) {
		this.nodeNameCond = nodeNameCond;
	}
	
	public void setObservedOutcome(String observedOutcome) {
		this.observedOutcome = observedOutcome;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	public void addConditionalNode(ConditionalNode node){
		nodeNameCond.add(node);
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getObservedOutcome() {
		return observedOutcome;
	}

	public List<ConditionalNode> getNodeNameCond() {
		return nodeNameCond;
	}

	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		String toReturn =  "Observation [nodeName=" + nodeName + ", observedOutcome=" + observedOutcome+ ", timestamp=" + timestamp+" ";
		for(int i=0; i<nodeNameCond.size(); i++){
			toReturn = toReturn+nodeNameCond.get(i).toString()+" ";
		}
		toReturn = toReturn+"]";
		return toReturn;
	}
	
	
}
