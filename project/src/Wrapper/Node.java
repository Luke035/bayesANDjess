package Wrapper;

import java.util.ArrayList;
import java.util.List;

public class Node{
	public final String name;		
	public final String[] parents;
	public final String[] values;
	public final double[] probabilities;
	
	public Node(String name, String[] values, String[] parents, 
			double[] probabilities) {
		this.name = name;
		this.parents = parents;
		this.values = values;
		this.probabilities = probabilities;
	}
	
	public Node(String name, String[] values, String[] parents) {
		this.name = name;
		this.parents = parents;
		this.values = values;
		this.probabilities = new double[]{};
	}
}