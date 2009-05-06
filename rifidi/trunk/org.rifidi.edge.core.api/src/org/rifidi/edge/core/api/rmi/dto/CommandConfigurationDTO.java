/**
 * 
 */
package org.rifidi.edge.core.api.rmi.dto;
//TODO: Comments
import java.io.Serializable;

import javax.management.AttributeList;

/**
 * @author kyle
 * 
 */
public class CommandConfigurationDTO implements Serializable {

	/** Default SerialVersionID */
	private static final long serialVersionUID = 1L;
	
	private String commandConfigID;
	private String commandConfigType;
	/** The list of attributes of the commandConfiguration */
	private AttributeList attributes;
	
	/**
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
	 * @return the commandConfigID
	 */
	public String getCommandConfigID() {
		return commandConfigID;
	}

	/**
	 * @return the commandConfigType
	 */
	public String getCommandConfigType() {
		return commandConfigType;
	}

	/**
	 * @return the attributes
	 */
	public AttributeList getAttributes() {
		return attributes;
	}
}
