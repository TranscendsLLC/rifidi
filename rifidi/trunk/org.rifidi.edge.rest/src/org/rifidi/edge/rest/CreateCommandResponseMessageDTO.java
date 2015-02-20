/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * 
 * @author Matthew Dean
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class CreateCommandResponseMessageDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8281848444556540826L;
	
	@XmlElement(name = "commandID")
	private String commandID;

	public String getCommandID() {
		return commandID;
	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

}
