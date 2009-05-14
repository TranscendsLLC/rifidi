
package org.rifidi.edge.core.api.rmi.dto;

import java.io.Serializable;

import javax.management.AttributeList;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class CommandConfigurationDTO implements Serializable {

	/** Default SerialVersionID */
	private static final long serialVersionUID = 1L;
	
	private String commandConfigID;
	private String commandConfigType;
	/** The list of attributes of the commandConfiguration */
	private AttributeList attributes;
	
	/**
	 * Constructor.  
	 * 
	 * @param commandConfigID
	 * @param commandConfigType
	 * @param readerFactoryID
	 */
	public CommandConfigurationDTO(String commandConfigID,
			String commandConfigType, AttributeList attributes) {
		super();
		this.commandConfigID = commandConfigID;
		this.commandConfigType = commandConfigType;
		this.attributes = attributes;
	}

	/**
	 * Returns the ID for this CommandConfiguration.  
	 * 
	 * @return the commandConfigID
	 */
	public String getCommandConfigID() {
		return commandConfigID;
	}

	/**
	 * Returns the type of the CommandConfiguration.  
	 * 
	 * @return the commandConfigType
	 */
	public String getCommandConfigType() {
		return commandConfigType;
	}

	/**
	 * Returns the AttributeList for this CommandConfiguration.  
	 * 
	 * @return the attributes
	 */
	public AttributeList getAttributes() {
		return attributes;
	}
}
