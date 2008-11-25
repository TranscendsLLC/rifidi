package org.rifidi.edge.client.properties.data;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Attribute;
import org.jdom.Element;

public class PropertyResponse {
	
	Element property;
	
	public PropertyResponse(Element e){
		this.property = e;
	}
	
	public String getElementValue(String elementName){
		return property.getChildText(elementName);
	}
	
	public boolean hasError(String elementName){
		Element widget = property.getChild(elementName);
		Attribute error = widget.getAttribute("error");
		if(error!=null){
			if(error.getValue().equalsIgnoreCase("true")){
				return true;
			}
		}
		
		return false;
	}
	
	public ArrayList<String> getElementNames(){
		List<Element>children = property.getChildren();
		ArrayList<String> names = new ArrayList<String>();
		for(Element e : children){
			names.add(e.getName());
		}
		return names;
	}
}
