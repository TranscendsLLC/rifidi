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
 * @author Matthew Dean - matt@transcends.co
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Command")
public class CommandNameDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4359786581033001139L;
	
	@XmlElement(name = "CommandID")
	private String commandID;
	
	@XmlElement(name = "CommandType")
	private String commandType;

	public String getCommandID() {
		return commandID;
	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

	public String getCommandType() {
		return commandType;
	}

	public void setCommandType(String commandType) {
		this.commandType = commandType;
	}
}
