package org.rifidi.edge.core.readerplugin.commands;

import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommandConfiguration {
	
	Document doc;
	
	public CommandConfiguration(Document doc) {
		this.doc = doc;
	}
	
	public String getCommandName() throws RifidiInvalidConfigurationException{
		NodeList nodeList = doc.getChildNodes();
		
		for(int i=0; i<nodeList.getLength(); i++){
			Node n = nodeList.item(i);
			if(n.getNodeType() == Element.ELEMENT_NODE){
				Element e = (Element)n;
				return e.getNodeName();
			}
		}
		throw new RifidiInvalidConfigurationException("Cannot find command name");
	}
	
	public Element getConfigAsElement() throws RifidiInvalidConfigurationException{
		NodeList nodeList = doc.getChildNodes();
		for(int i=0; i<nodeList.getLength(); i++){
			Node n = nodeList.item(i);
			if(n.getNodeType() == Element.ELEMENT_NODE){
				Element e = (Element)n;
				return e;
			}
		}
		throw new RifidiInvalidConfigurationException("Cannot find command name");
	}

}
