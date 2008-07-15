package org.rifidi.edge.readerplugin.alien.messages;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

public class Property {
	
	private String name;
	
	private String value;
	
	public Property (String name, String value){
		this.name = name;
		this.value = value;
	}


	/**
	 * @return the name
	 */
	@XmlAttribute
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the value
	 */
	@XmlValue
	public String getValue() {
		return value;
	}

	/**
	 * @param value the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}
}
