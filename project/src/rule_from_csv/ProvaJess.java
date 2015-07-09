package rule_from_csv;

import jess.JessException;
import jess.Rete;

public class ProvaJess{
	public static void main(String [] args){
		Rete engine = new Rete();
		
		try {
			System.out.println(engine.executeCommand("(reset)"));
			System.out.println(engine.executeCommand("(defrule Rule1 (Tag 2 3 4) => (assert(Artist 3)))"));
			System.out.println(engine.executeCommand("(defrule Rule2 (Tag 2) => (assert (Artist 4)))"));
			System.out.println(engine.executeCommand("(assert (Tag 2 3 4))"));
			System.out.println(engine.executeCommand("(assert (Tag 2))"));
			System.out.println(engine.executeCommand("(facts)"));
			System.out.println(engine.executeCommand("(watch all)"));
			System.out.println(engine.executeCommand("(run)"));
			System.out.println(engine.executeCommand("(facts)"));
		} catch (JessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
