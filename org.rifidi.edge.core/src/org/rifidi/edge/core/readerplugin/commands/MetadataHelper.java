package org.rifidi.edge.core.readerplugin.commands;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MetadataHelper {
	
	public static Element toXML(Document doc, IntegerMetadata metadata){
		Element element = doc.createElement("integer");
		
		Element name = doc.createElement("name");
		name.appendChild(doc.createTextNode(metadata.name()));
		element.appendChild(name);
		
		Element editable = doc.createElement("editable");
		editable.appendChild(doc.createTextNode(Boolean.toString(metadata.editable())));
		element.appendChild(editable);
		
		Element displayName = doc.createElement("displayname");
		displayName.appendChild(doc.createTextNode(metadata.displayName()));
		element.appendChild(displayName);
		
		Element defaultValue = doc.createElement("defaultvalue");
		defaultValue.appendChild(doc.createTextNode(Integer.toString(metadata.defaultValue())));
		element.appendChild(defaultValue);
		
		Element maxvalue = doc.createElement("maxvalue");
		maxvalue.appendChild(doc.createTextNode(Integer.toString(metadata.maxValue())));
		element.appendChild(maxvalue);
		
		Element minvalue = doc.createElement("minvalue");
		minvalue.appendChild(doc.createTextNode(Integer.toString(metadata.minValue())));
		element.appendChild(minvalue);
		
		return element;
	}
	
	public static Element toXML(Document doc, StringMetadata metadata){
		Element element = doc.createElement("string");
		
		Element name = doc.createElement("name");
		name.appendChild(doc.createTextNode(metadata.name()));
		element.appendChild(name);
		
		Element editable = doc.createElement("editable");
		editable.appendChild(doc.createTextNode(Boolean.toString(metadata.editable())));
		element.appendChild(editable);
		
		Element displayName = doc.createElement("displayname");
		displayName.appendChild(doc.createTextNode(metadata.displayName()));
		element.appendChild(displayName);
		
		Element defaultValue = doc.createElement("defaultvalue");
		defaultValue.appendChild(doc.createTextNode(metadata.defaultValue()));
		element.appendChild(defaultValue);
		
		Element regex = doc.createElement("regex");
		regex.appendChild(doc.createTextNode(metadata.regex()));
		element.appendChild(regex);
		
		return element;
	}

}
