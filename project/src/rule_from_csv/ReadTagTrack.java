package rule_from_csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadTagTrack {
	public String csvPath = "./tag-track-onlyIDS.csv";
	
	private BufferedReader br=null;
	
	public ReadTagTrack(){
		File f = new File (csvPath);
		try {
			br = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void readFile() throws IOException{
		br.readLine(); //Riga di  header
		
		String line = br.readLine();
		int i=0;
		while(line!=null){
			String [] splittedLine = line.split(";");
			
			String rhs = checkText(splittedLine[0],'"','\0');
			
			String lhsTot = splittedLine[1];
			
			String [] lhsArray = lhsTot.split(",");
			ArrayList<String> lhsList = new ArrayList<String>();
			
			for(int k=0; k<lhsArray.length; k++){
				lhsList.add(checkText(lhsArray[k],'"','\0'));
			}
			
			//Stampa
			SimpleRule sr = new SimpleRule(lhsList,rhs,"Tag","Track");
			System.out.println(sr.generateJESSRule(i));
			/*System.out.println("Regola "+(i+1)+":");
			for(int j=0; j<lhsList.size(); j++){
				System.out.print("LHS: "+lhsList.get(j)+" ");
			}
			System.out.println();
			System.out.println("RHS: "+rhs);*/
			System.out.println("------------------------------------");
			
			i++;
			
			line = br.readLine();
		}
	}
	
	private String checkText(String toCheck, char toReplace, char substitute){
		return toCheck.replace(toReplace, substitute);
	}
	
	public static void main(String [] args){
		ReadTagTrack rtt = new ReadTagTrack();
		try {
			rtt.readFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
