package org.rifidi.edge.core.readerplugin.commands;

import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class CommandConfiguration {
	
	Document doc;
	
	public CommandConfiguration(Document doc) {
		this.doc = doc;
	}
	
	public String getCommandName() throws RifidiInvalidConfigurationException{
/*		NodeList nodeList = doc.getChildNodes();
		if(nodeList.getLength()!=1){
			//TODO throw Invalid Parameter Exception
		}
		if(nodeList.item(0).getNodeType()!=Element.ELEMENT_NODE){
			//TODO: throw Invalid Parameter Exception
		}
		Element e = (Element)nodeList.item(0);
		return e.getNodeName();*/
		return null;
	}
	
	public Element getConfigAsElement() throws RifidiInvalidConfigurationException{
		/*if(doc.getFirstChild().getNodeType() == Element.ELEMENT_NODE){
			Node
		}else{
			//TODO: throw exception
		}*/
		return null;
	}

}
