package xml_handler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParser {
	public List<Observation> parseXML(String pathName) throws
	ParserConfigurationException,SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();

		// Load the input XML document, parse it and return an instance of the
		// Document class.
		Document document = builder.parse(new File(pathName));

		NodeList nodeList = document.getDocumentElement().getChildNodes();

		ArrayList<Observation> toReturn = new ArrayList<Observation>();

		for(int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);


			//String nodeName = node.getAttributes().getNamedItem("node_name").getNodeValue();
			//System.out.println("Node name: "+nodeName);

			if(node.getNodeType() == Node.ELEMENT_NODE){
				Observation obs = new Observation();

				Element elem = (Element) node;

				//System.out.println("Observation :"+elem.getElementsByTagName("observation")
				// .item(0).getChildNodes().item(0).getNodeValue());
				obs.setNodeName(elem.getElementsByTagName(XML_tags.NODE_NAME)
						.item(0).getChildNodes().item(0).getNodeValue());
				obs.setObservedOutcome(elem.getElementsByTagName(XML_tags.OBSERVED_OUTCOME)
						.item(0).getChildNodes().item(0).getNodeValue());
				obs.setTimestamp(Long.parseLong(elem.getElementsByTagName(XML_tags.TIMESTAMP)
						.item(0).getChildNodes().item(0).getNodeValue()));

				try{
					NodeList nodesNames = elem.getElementsByTagName(XML_tags.NODE_NAME_COND);
					NodeList nodesOutcomes = elem.getElementsByTagName(XML_tags.OBSERVED_OUTCOME_COND);

					if(nodesNames.getLength()!=nodesOutcomes.getLength())
						throw new UncorrectConditionalNodeException("Expected "+nodesNames.getLength()+" conditional outcomes, get "+nodesOutcomes.getLength());

					if(nodesNames.getLength() >0){
						for(int j=0; j<nodesNames.getLength();j++){
							ConditionalNode conditionalNode = new ConditionalNode();
							conditionalNode.setNodeName(nodesNames.item(j).getTextContent());
							conditionalNode.setOutcome(nodesOutcomes.item(j).getTextContent());
							obs.addConditionalNode(conditionalNode);
						}
					}
				}catch(Exception e){
					//Do nothing
				}
				toReturn.add(obs);
			}
		}
		return toReturn;
	}
}
