package Wrapper;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class NetLoader {
	// static String csvFile = "C:\\Users\\Luca\\Desktop\\rete.csv";
	// static String csvFile = "C:\\Users\\Andre\\Dropbox\\Uni\\Progetto MPD\\rete.csv";
	private static String cvsSplitBy = ";";
	private static String blockSplitBy = ":";
	private static Wrapper wNet;

	public static Wrapper getNet(){
		return wNet;
	}

	public static void buildNet(String csvFile){
		List<Node> nodes = readNodes(csvFile);
		wNet = new Wrapper();
		for (Node n:nodes){
			try{
				wNet.addNode(n.name, n.values, n.parents);
				if (n.probabilities.length>0)
					wNet.setProbabilites(n.name, n.probabilities);
			}catch(JayesException e){
				e.printStackTrace();
			}
		}
	}

	public static void buildNet(BufferedReader br){
		List<Node> nodes = readNodes(br);
		wNet = new Wrapper();
		for (Node n:nodes){
			try{
				wNet.addNode(n.name, n.values, n.parents);
				if (n.probabilities.length>0)
					wNet.setProbabilites(n.name, n.probabilities);
			}catch(JayesException e){
				e.printStackTrace();
			}
		}
	}

	private static List<Node> readNodes(String csvFile){
		BufferedReader br = null;		
		List<Node> nodes = new ArrayList<Node>();

		try {
			br = new BufferedReader(new FileReader(csvFile));
			nodes = readNodes(br);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return nodes;
	}

	private static List<Node> readNodes(BufferedReader br){
		String line = "";
		List<Node> nodes = new ArrayList<Node>();
		try {
			while ((line = br.readLine()) != null) {
				String[] linea = line.split(cvsSplitBy);

				if (linea[0].equals("x")){	
					String[] parents = new String[]{};
					if (linea.length>3 && !linea[3].equals("")){
						parents = linea[3].split(blockSplitBy);
					}
					double[] probabilities = new double[]{};
					if (linea.length>4 && !linea[4].equals("")){
						probabilities = parseDoubleArray(linea[4].split(blockSplitBy));	
					}

					nodes.add(new Node(linea[1], 
							linea[2].split(blockSplitBy),
							parents, probabilities));
				}				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return nodes;
	}

	private static double[] parseDoubleArray(String[] sArray){
		double[] dArray = new double[sArray.length];
		int i = 0;
		for (String s:sArray){
			//System.out.println("S: "+s);
			dArray[i] = Double.parseDouble(s);
			i++;
		}
		return dArray;
	}
}