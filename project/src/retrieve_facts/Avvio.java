package retrieve_facts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import bayesANDjess.Observation;
import jess.JessException;
import jess.Rete;
import jess.Value;

public class Avvio {
	public static String path = "./staticRulesArtist-Tag.txt";
	
	public static String [] parseRules(String path){
		File f = new File(path);
		BufferedReader reader = null;
		try { 
			 reader = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
		//List<String> commands = new ArrayList<String>();
		String [] commands = null;
		try{
			String line = reader.readLine();
			String lines = "";
			String command = "";
			while(line!=null){
				String replace1 = line.replace('\n', ' ');
				String replace2 = replace1.replace('	', ' ');
				lines += replace2;/*
				if(line.contains(";")){
					command += line.replace(';', ' ');
					commands.add(command);
					command = "";
				}
				command += line.replace('\n', ' ');
				line = reader.readLine();*/
				line = reader.readLine();
			}
			
			commands = lines.split(";");
			
		}catch(Exception e){ /*Do Nothing*/}
		
		
		return commands;
	}
	
	public static void prova1(){

		String [] rules = parseRules(path);

		
		Rete engine = new Rete();
				
		for(String r:rules){
			try {
				String toAdd = r.replace('\0', ' ');
				System.out.println("Command: "+toAdd);
				engine.executeCommand(toAdd);
				
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<Observation> facts = new ArrayList<Observation>();
		
		facts.add(new Observation("Artist","11936"));
		facts.add(new Observation("Artist","2"));
		facts.add(new Observation("Artist","3"));
		facts.add(new Observation("Artist","4"));
		

		for(Observation f:facts){
			try {
				System.out.println(engine.executeCommand("(assert ("+f.getName()+" "+f.getValue()+"))"));
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
			
		try{
		
		System.out.println(engine.executeCommand("(watch all)"));
		System.out.println(engine.executeCommand("(facts)"));
		System.out.println(engine.executeCommand("(run)"));
		System.out.println(engine.executeCommand("(facts)"));
		}catch(JessException e){}
		
		String result = "";
		
		Iterator iterator = engine.listFacts();
		List<Integer> tags = parseResult(iterator,"Tag");
		System.out.print("Tag: ");
		for(int t:tags){
			System.out.print(t+",");
		}
		//System.out.println("Result: "+result);
	}
	
	private static List<Integer> parseResult(Iterator iterator, String toFetch){
		List<Integer> results = new ArrayList<Integer>();
	    while(iterator.hasNext()) {
	         String element = iterator.next().toString();
	         System.out.println("Element:"+element);
	         String []splitted = element.split("::");
	         System.out.println("Splitted:"+splitted[0]+","+splitted[1]);
	         String []name_value = splitted[1].split(" ");
        	 System.out.println("Name_value:"+name_value[0]+","+name_value[1]);
	         if(name_value[0].equals(toFetch)){
	        	 String cleaned = name_value[1].replace(')', '\0');
	        	 char[] resultsArray = cleaned.toCharArray();
	        	 String partial="";
	        	 for(char c:resultsArray){
	        		 if(c==','){
	        			 results.add(Integer.parseInt(partial));
	        			 partial="";
	        		 }else{
	        			 partial +=c;
	        		 }
	        		 
	        	 }
	         }
	     }
	    
	    return results;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		prova1();
	}

}
