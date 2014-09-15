package org.rifidi.edge.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Response")
public class RestResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 618168723573019895L;
	
	@XmlElement(name="Message")
	public String message;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}	
}
