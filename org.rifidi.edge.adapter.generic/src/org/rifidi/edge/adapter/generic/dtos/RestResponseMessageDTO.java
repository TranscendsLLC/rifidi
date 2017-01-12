package org.rifidi.edge.adapter.generic.dtos;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class RestResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 618168723573019895L;
	
	@XmlElement(name="message")//Success / Fail
	public String message;
	
	@XmlElement(name="state")//Exception / (empty for Success)
	public String state;
	
	@XmlElement(name="description")//properties that fail in the create command or reader, prop1=val1|prop2=val2
	public String description;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	
}
