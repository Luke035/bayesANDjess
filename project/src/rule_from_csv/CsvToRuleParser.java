package rule_from_csv;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class CsvToRuleParser {

	private String csvPath;
	private String lhsName;
	private String rhsName;
	private int columnLhs;
	private int columnRhs;
	private BufferedReader bufferedReader;
	private char columnSeparator;
	private char listSeparator;


	public CsvToRuleParser(String csvPath, String lhsName, String rhsName,
			int columnLhs, int columnRhs, char columnSeparator, char listSeparator) {
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

	public List<SimpleRule> readFile() throws IOException{
		ArrayList<SimpleRule> rulesList = new ArrayList<SimpleRule>();

		this.bufferedReader.readLine(); //Riga di  header

		String line = this.bufferedReader.readLine();
		int i=0;
		while(line!=null){
			String [] splittedLine = line.split(""+this.columnSeparator);

			String rhs = checkText(splittedLine[this.columnRhs],'"','\0');

			String lhsTot = splittedLine[this.columnLhs];

			String [] lhsArray = lhsTot.split(""+this.listSeparator);
			ArrayList<String> lhsList = new ArrayList<String>();

			for(int k=0; k<lhsArray.length; k++){
				lhsList.add(checkText(lhsArray[k],'"','\0'));
			}

			//Stampa
			SimpleRule sr = new SimpleRule(lhsList,rhs,this.lhsName,this.rhsName);
			rulesList.add(sr);
			
			line = this.bufferedReader.readLine();
		}
		
		return rulesList;
	}


	private String checkText(String toCheck, char toReplace, char substitute){
		return toCheck.replace(toReplace, substitute);
	}
	
	public static void main(String [] args){
		CsvToRuleParser parser = new CsvToRuleParser("./tag-track-onlyIDS.csv","Tag","Track",
				1,0,';',',');
		
		CsvToRuleParser parser2 = new CsvToRuleParser("./artist-tag-onlyIDS.csv","Tag","Artist",
				1,0,';',',');
		
		CsvToRuleParser parser3 = new CsvToRuleParser("./artist-tag-onlyIDS.csv","Artist","Tag",
				0,1,';',',');
		
		File f1 = new File("./staticRulesTag-track.txt");
		File f2 = new File("./staticRulesTag-artist.txt");
		File f3 = new File("./staticRulesArtist-Tag.txt");
		
		PrintWriter printWriter1 = null;
		PrintWriter printWriter2 = null;
		PrintWriter printWriter3 = null;
		try {
			printWriter1 = new PrintWriter(f1);
			printWriter2 = new PrintWriter(f2);
			printWriter3 = new PrintWriter(f3);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		try {
			ArrayList<SimpleRule> rulesList1 = (ArrayList<SimpleRule>) parser.readFile();
			ArrayList<SimpleRule> rulesList2  = (ArrayList<SimpleRule>) parser2.readFile();
			ArrayList<SimpleRule> rulesList3  = (ArrayList<SimpleRule>) parser3.readFile();
			for(int i=0; i<rulesList2.size(); i++){
				printWriter1.println(rulesList1.get(i).generateJESSRule(i)+";");
				printWriter2.println(rulesList2.get(i).generateJESSRule(i)+";");
				printWriter3.println(rulesList3.get(i).generateJESSRule(i)+";");
				//System.out.println(rulesList.get(i).generateJESSRule(i));
				//System.out.println(rulesList2.get(i).generateJESSRule(i));
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
