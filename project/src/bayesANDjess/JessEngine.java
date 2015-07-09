package bayesANDjess;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jess.JessException;
import jess.RU;
import jess.Rete;
public class JessEngine {
	private Rete engine;
	private ArrayList<String> templateNames;
	public JessEngine(){
		this.engine = new Rete();
		templateNames = new ArrayList<String>();
		//engine.executeCommand("(defrule DepositoScelta (Mezzo ?m) => (store \"Mezzo\" ?m))");}
	}
	
	private String checkBlankSpace(String ruleName){
		char [] stringArray = ruleName.toCharArray();
		String toReturn="";
		for(int i=0; i<stringArray.length; i++){
			if(stringArray[i]==' ')
				toReturn  = toReturn+"_";
			else
				toReturn = toReturn+stringArray[i];
		}
		return toReturn;
	}
	
	public void addTemplate(String toInfer){
		String command = "(deftemplate Result_"+checkBlankSpace(toInfer)+" (slot "+checkBlankSpace(toInfer)+") (slot Reliability))";
		try {
			System.out.println("Inviato comando "+command);
			System.out.println(engine.executeCommand(command));
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.templateNames.add(checkBlankSpace(toInfer));
	}
	
	public void addRule(String ruleName, String lhsRule, String rhsRule){
		//TODO check assert in rhsRule
		String command = "(defrule "+ruleName+" ";
		command = command+lhsRule+" => "+rhsRule; //TODO check parentesis
		System.out.println("Invio comando "+command);
		try {
			System.out.println(engine.executeCommand(command));
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String addRule(String command){
		try {
			return ""+engine.executeCommand(command);
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
	}
	
	public void defStoreRules(){
		//engine.executeCommand("(defrule DepositoScelta (Mezzo ?m) => (store \"Mezzo\" ?m))");}
		for(int i=0; i<this.templateNames.size(); i++){
			String command = "(defrule DepositoScelta"+i+
					" (Result_"+templateNames.get(i)+" ("+templateNames.get(i)+" ?m) (Reliability ?r)) => (store \""+templateNames.get(i)+"\" ?m))";
			System.out.println("Inviato comando "+command);
			try {
				System.out.println(engine.executeCommand(command));
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private List<Integer> parseResult(Iterator iterator, String toFetch) throws NotSimpleIDResultsException{
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
	        			 try{
	        			 results.add(Integer.parseInt(partial));
	        			 }catch(Exception e){
	        				 throw new NotSimpleIDResultsException("Not ID result found");
	        			 }
	        			 partial="";
	        		 }else{
	        			 partial +=c;
	        		 }
	        		 
	        	 }
	         }
	     }
	    
	    return results;
	}
	
	public String infer(ArrayList<Observation> observations, String toFetch){
		defStoreRules();
		
		//Store facts
		for(int i=0; i<observations.size(); i++){
			//engine.store(observations.get(i).getName(), 
					//new Value(observations.get(i).getValue(), RU.ATOM));
			try {
				System.out.println(engine.executeCommand("(assert ("+observations.get(i).getName()+" "+observations.get(i).getValue()+"))"));
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Aggiunto fatto "+observations.get(i).getName()+" value: "+observations.get(i).getValue());
		}
		//run rules
		try {
			System.out.println(engine.executeCommand("(watch all)"));
			System.out.println(engine.executeCommand("(facts)"));
			System.out.println(engine.executeCommand("(run)"));
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = "";
		if(engine.fetch(toFetch)!=null){
			try {
				result = engine.fetch(toFetch).stringValue(engine.getGlobalContext());
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public List<Integer> inferIDs(ArrayList<Observation> observations, String toFetch) throws NotSimpleIDResultsException{
		defStoreRules();
		
		//Store facts
		for(int i=0; i<observations.size(); i++){
			//engine.store(observations.get(i).getName(), 
					//new Value(observations.get(i).getValue(), RU.ATOM));
			try {
				System.out.println(engine.executeCommand("(assert ("+observations.get(i).getName()+" "+observations.get(i).getValue()+"))"));
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Aggiunto fatto "+observations.get(i).getName()+" value: "+observations.get(i).getValue());
		}
		//run rules
		try {
			System.out.println(engine.executeCommand("(watch all)"));
			System.out.println(engine.executeCommand("(facts)"));
			System.out.println(engine.executeCommand("(run)"));
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = "";
		if(engine.fetch(toFetch)!=null){
			try {
				result = engine.fetch(toFetch).stringValue(engine.getGlobalContext());
			} catch (JessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Iterator iterator = engine.listFacts();
		List<Integer> results = parseResult(iterator,toFetch);
		
		return results;
	}
	
}
