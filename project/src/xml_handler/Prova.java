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

public class Prova {
	static String pathName = "./prova.xml";
	
	public static void main(String[] args) {
		
		XmlParser parser = new XmlParser();
		ArrayList<Observation> lista = null;
		try {
			lista = (ArrayList<Observation>) parser.parseXML(pathName);
		} catch (ParserConfigurationException | SAXException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		for(int i=0; i<lista.size();i++){
			System.out.println(lista.get(i).toString());
			System.out.println("--------------------------------");
		}
		
	}

}
