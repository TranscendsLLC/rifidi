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
 * 
 * 
 * @author Matthew Dean
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "executingcommand")
public class ExecutingCommandDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1759454640833382091L;

	@XmlElement(name = "commandID")
	private String commandID;
	
	@XmlElement(name = "factoryID")
	private String factoryID;
	
	@XmlElement(name = "interval")
	private Long interval;

	public String getCommandID() {
		return commandID;
	}

	public void setCommandID(String commandID) {
		this.commandID = commandID;
	}

	public Long getInterval() {
		return interval;
	}

	public void setInterval(Long interval) {
		this.interval = interval;
	}

	public String getFactoryID() {
		return factoryID;
	}

	public void setFactoryID(String factoryID) {
		this.factoryID = factoryID;
	}
	
	
}
