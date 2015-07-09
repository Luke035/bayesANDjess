package simulatore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;

import Wrapper.ComposedProbability;
import Wrapper.NodeProbability;

public class PrinterStructure {
	private NodeProbability np;
	private PrintWriter pw;
	private String path;
	
	public PrinterStructure(NodeProbability np){
		this.np = np;
		String path ="./probData"+np.getNode()+".csv";
		
		File f = new File(path);
		
		pw=null;
		
		try {
			pw = new PrintWriter(f);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String header = "run_number,gamma";
		List<ComposedProbability> cpList =np.getProbabilities();
		
		for(ComposedProbability cp:cpList){
			header += ",prob"+cp.getNodeOutcome()+",obs"+cp.getNodeOutcome()+",likelihood"+cp.getNodeOutcome();
		}
		
		pw.println(header);
	}
	
	public void closeStream(){
		pw.close();
	}

	public NodeProbability getNp() {
		return np;
	}

	public PrintWriter getPw() {
		return pw;
	}

	public String getPath() {
		return path;
	}
}
