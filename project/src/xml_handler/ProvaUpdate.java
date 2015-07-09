package xml_handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import Wrapper.ComposedProbability;
import Wrapper.JayesException;
import Wrapper.NodeProbability;
import Wrapper.NodeWithParentsException;
import Wrapper.ProbabilityNotFoundException;
import Wrapper.Wrapper;
import bayesANDjess.BayesNet;

import org.eclipse.recommenders.jayes.BayesNode;
import org.xml.sax.SAXException;

public class ProvaUpdate {

	public static void main(String[] args) {
		BayesNet bn = new BayesNet();
		
		NetUpdater netUpdater = new NetUpdater(bn);
		
		//Recupero nuove osservazioni
		XmlParser observationParser = new XmlParser();
		List<Observation> observationList = null;
		try {
			observationList = observationParser.parseXML("./prova.xml");
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Definizione lista di tutti i nodi della rete (vanno controllati tutti)
		List<String> nodesList = new ArrayList<String>();
		nodesList.add("temperatura");
		nodesList.add("condizioni_meteo");
		nodesList.add("mezzo");
		nodesList.add("km_to_run");
		nodesList.add("momento_viaggio");
		nodesList.add("mood_meteo");
		nodesList.add("travel_mood");
		nodesList.add("tag_cluster");
		
		//Timestamp attuale
		GregorianCalendar calendar = new GregorianCalendar();
		double t = (double) calendar.getTimeInMillis();
		double gamma = 1.0;
		for(String node:nodesList){
			String [] parents = bn.getWrapper().getParents(node);
			if(parents==null || parents.length==0){
				netUpdater.updateNoParentsNode(observationList,node,t,gamma);
				//updateNoParentsNode(bn,observationList,node,t,gamma);
			}else{
				netUpdater.updateNodeWithParents(observationList, node, t, gamma);
				//updateNodeWithParents(bn,observationList,node,t,gamma);
			}
		}
	}
}
