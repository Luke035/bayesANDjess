package bayesANDjess;
import java.util.ArrayList;


public class Node {
	private String nodeName;
	private ArrayList<Value> values;
	
	public Node(String nodeName) {
		super();
		this.nodeName = nodeName;
		values = new ArrayList<Value>();
	}
	
	public void addValue(Value v){
		values.add(v);
	}

	public String getNodeName() {
		return nodeName;
	}

	public ArrayList<Value> getValues() {
		return values;
	}
}
