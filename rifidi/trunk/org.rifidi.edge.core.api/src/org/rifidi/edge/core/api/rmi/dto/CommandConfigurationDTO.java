/**
 * 
 */
package org.rifidi.edge.core.api.rmi.dto;

import java.io.Serializable;

/**
 * @author kyle
 * 
 */
public class CommandConfigurationDTO implements Serializable {

	/** Default SerialVersionID */
	private static final long serialVersionUID = 1L;
	
	private String commandConfigID;
	private String commandConfigType;
	private String readerFactoryID;
	
	/**
	 * @param commandConfigID
	 * @param commandConfigType
	 * @param readerFactoryID
	 */
	public CommandConfigurationDTO(String commandConfigID,
			String commandConfigType, String readerFactoryID) {
		super();
		this.commandConfigID = commandConfigID;
		this.commandConfigType = commandConfigType;
		this.readerFactoryID = readerFactoryID;
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
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
	

}
