package rule_from_csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class AssoRulesToJessParser {
	private String csvPath;
	private String lhsName;
	private String rhsName;
	private int columnLhs;
	private int columnRhs;
	private BufferedReader bufferedReader;
	private char columnSeparator;
	private char listSeparator;
	private int columnWeight=7;
	
	public AssoRulesToJessParser(String csvPath, String lhsName,
			String rhsName, int columnLhs, int columnRhs,
		    char columnSeparator,
			char listSeparator) {
		super();
		this.csvPath = csvPath;
		this.lhsName = lhsName;
		this.rhsName = rhsName;
		this.columnLhs = columnLhs;
		this.columnRhs = columnRhs;
		this.columnSeparator = columnSeparator;
		this.listSeparator = listSeparator;
		
		File f = new File (csvPath);
		try {
			bufferedReader = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<SimpleRule> parseFile(double minWeight) throws IOException{
		ArrayList<SimpleRule> ruleList = new ArrayList<SimpleRule>();
		
		this.bufferedReader.readLine(); //Riga di  header

		String line = this.bufferedReader.readLine();
		while(line!=null){
			String [] splittedLine = line.split(""+this.columnSeparator);

			Double ruleWeight = Double.parseDouble(splittedLine[this.columnWeight]);
			if(ruleWeight >= minWeight){
				String rhs = checkText(splittedLine[this.columnRhs],'"','\0');

				String lhsTot = splittedLine[this.columnLhs];

				String [] lhsArray = lhsTot.split(""+this.listSeparator);
				ArrayList<String> lhsList = new ArrayList<String>();

				for(int k=0; k<lhsArray.length; k++){
					lhsList.add(checkText(lhsArray[k],'"','\0'));
				}

				//Stampa
				SimpleRule sr = new SimpleRule(lhsList,rhs,this.lhsName,this.rhsName);
				ruleList.add(sr);
			}

			line = this.bufferedReader.readLine();
		}

		return ruleList;
	}
	
	private String checkText(String toCheck, char toReplace, char substitute){
		return toCheck.replace(toReplace, substitute);
	}
	
	public static void main(String [] args){
		AssoRulesToJessParser parser = new AssoRulesToJessParser(
				"./assosiationRules-IDS.csv", "Artist", "Artist",
				0,1,';',','
				);
		
		File f = new File("./staticRules1.txt");
		
		PrintWriter printWriter = null;
		try {
			printWriter = new PrintWriter(f);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			ArrayList<SimpleRule> ruleList = (ArrayList<SimpleRule>) parser.parseFile(0.6);
			
			for(int i=0; i<ruleList.size();i++){
				printWriter.println(ruleList.get(i).generateJESSRule(i)+";");
				System.out.println(ruleList.get(i).generateJESSRule(i));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
