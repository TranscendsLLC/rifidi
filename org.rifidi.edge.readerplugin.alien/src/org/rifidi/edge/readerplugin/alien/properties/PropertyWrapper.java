/*
 *  PropertiesWrapper.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.readerplugin.alien.properties;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * @author kyle
 *
 */
public class PropertyWrapper {
	
	private Element property;
	
	public PropertyWrapper(Element property){
		this.property = property;
	}
	
	public String getPropertyName(){
		return property.getNodeName();
	}
	
	public String getElementValue(String elementName){
		NodeList nl = property.getChildNodes();
		for(int i=0; i<nl.getLength(); i++){
			Node n = nl.item(i);
			if(n.getNodeName().equalsIgnoreCase(elementName)){
				NodeList nl2 = n.getChildNodes();
				for(int j=0; j<nl2.getLength(); j++){
					Node n2 = nl2.item(j);
					if(n2.getNodeType() == Node.TEXT_NODE){
						return ((Text)n2).getData();
					}
				}
			}
		}
		return null;
	}

}
