package retrieve_facts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import jess.JessException;
import jess.Rete;

public class ProvaJess {
	
	public static void commands() throws JessException{
		Rete engine = new Rete();
		
		System.out.println("Template1: "+engine.executeCommand("(deftemplate Result_Mood_meteo (slot Mood_meteo) (slot Reliability))"));
	
		System.out.println("Template2: "+engine.executeCommand("(deftemplate Result_Travel_mood (slot Travel_mood) (slot Reliability))"));
		
		System.out.println("Template3: "+engine.executeCommand("(deftemplate Result_Tag_cluster (slot Tag_cluster) (slot Reliability))"));
		
		System.out.println("Rule: "+engine.executeCommand("(defrule Mood_meteoTriste98 (Temperatura 5_15)  (Condizioni_meteo Temporale)  (Mezzo Auto)  (Km_to_run 10_30)  (Momento_viaggio Giorno_feriale) =>(assert (Result_Mood_meteo (Mood_meteo Triste) (Reliability 0.4500000000000001))))"));
		
		//Fatti
		System.out.println("Temperatura: "+engine.executeCommand("(assert (Temperatura 5_15))"));
		System.out.println("Condizioni meteo: "+engine.executeCommand("(assert (Condizioni_meteo Temporale))"));
		System.out.println("Mezzo: "+engine.executeCommand("(assert (Mezzo Auto))"));
		System.out.println("KM to run: "+engine.executeCommand("(assert (Km_to_run 10_30))"));
		System.out.println("Momento viaggio: "+engine.executeCommand("(assert (Momento_viaggio Giorno_feriale))"));
		
		System.out.println(engine.executeCommand("(watch all)"));
		System.out.println(engine.executeCommand("(facts)"));
		System.out.println(engine.executeCommand("(run)"));
		System.out.println(engine.executeCommand("(facts)"));
	}
	
	public static void readFile() throws IOException, JessException{
		BufferedReader br = null;
		Rete engine = new Rete();
		
		System.out.println("Template1: "+engine.executeCommand("(deftemplate Result_Mood_meteo (slot Mood_meteo) (slot Reliability))"));
		
		System.out.println("Template2: "+engine.executeCommand("(deftemplate Result_Travel_mood (slot Travel_mood) (slot Reliability))"));
		
		System.out.println("Template3: "+engine.executeCommand("(deftemplate Result_Tag_cluster (slot Tag_cluster) (slot Reliability))"));
		
		try {
			br = new BufferedReader(new FileReader(new File("./net_musictext_dynamic.clp")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String line = br.readLine();
		String command = "";
		while(line!=null){
			if(line.contains(";")){
				command += line.replace(';', ' ');
				//System.out.println("Command: "+command);
				engine.executeCommand(command);
				command = "";
			}else{
				 command += line;
			}
			//System.out.println("Line: "+line);
			line = br.readLine();
		}
		
		//Store rules
		//System.out.println("Store rule: "+engine.executeCommand("(defrule DepositoCluster (Result_Tag_cluster (Tag_cluster ?tc) (Reliability ?r)) => (store \"Tag_cluster\" ?tc))"));
		//Fatti
		System.out.println("Temperatura: "+engine.executeCommand("(assert (Temperatura 5_15))"));
		System.out.println("Condizioni meteo: "+engine.executeCommand("(assert (Condizioni_meteo Temporale))"));
		System.out.println("Mezzo: "+engine.executeCommand("(assert (Mezzo Auto))"));
		System.out.println("KM to run: "+engine.executeCommand("(assert (Km_to_run 10_30))"));
		System.out.println("Momento viaggio: "+engine.executeCommand("(assert (Momento_viaggio Giorno_feriale))"));
				
		System.out.println(engine.executeCommand("(watch all)"));
		System.out.println(engine.executeCommand("(facts)"));
		System.out.println(engine.executeCommand("(run)"));
		System.out.println(engine.executeCommand("(facts)"));
		
		String result = "";
		if(engine.fetch("Tag_cluster")!=null){
			try {
				result = engine.fetch("Tag_cluster").stringValue(engine.getGlobalContext());
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println("Risultato: "+result);
	}
	
	public static void main(String[] args) {
		/*try {
			commands();
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		try {
			readFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
