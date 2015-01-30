/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author matt
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "commandmetadata")
public class CommandMetadataDTO implements Serializable {

	private static final long serialVersionUID = 2799161673076524544L;
	
	@XmlElement(name = "name")
	private String name;
	
	@XmlElement(name = "defaultvalue")
	private String defaultValue;
	
	@XmlElement(name = "description")
	private String description;
	
	@XmlElement(name = "type")
	private String type;
	
	@XmlElement(name = "maxvalue")
	private String maxValue;
	
	@XmlElement(name = "minvalue")
	private String minValue;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(String maxValue) {
		this.maxValue = maxValue;
	}

	public String getMinValue() {
		return minValue;
	}

	public void setMinValue(String minValue) {
		this.minValue = minValue;
	}
	
}
