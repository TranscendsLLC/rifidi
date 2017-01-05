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
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "readermetadata")
public class ReaderMetadataDTO implements Serializable {

	private static final long serialVersionUID = -4129940570200035491L;
	
	@XmlElement(name = "name")
	private String name;
	
	@XmlElement(name = "displayname")
	private String displayName;
	
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
	
	@XmlElement(name = "category")
	private String category;
	
	@XmlElement(name = "writable")
	private Boolean writable;
	
	@XmlElement(name = "ordervalue")
	private Float orderValue;

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

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Boolean getWritable() {
		return writable;
	}

	public void setWritable(Boolean writable) {
		this.writable = writable;
	}

	public Float getOrderValue() {
		return orderValue;
	}

	public void setOrderValue(Float orderValue) {
		this.orderValue = orderValue;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}
