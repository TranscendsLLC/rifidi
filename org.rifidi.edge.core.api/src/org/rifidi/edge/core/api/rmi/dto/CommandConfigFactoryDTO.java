/**
 * 
 */
package org.rifidi.edge.core.api.rmi.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * @author kyle
 *
 */
public class CommandConfigFactoryDTO implements Serializable{
	
	/***/
	private static final long serialVersionUID = 1L;

	private String readerFactoryID;
	
	private Set<String> commandConfigTypeIDs;

	/**
	 * @param readerFactoryID
	 * @param commandConfigTypeIDs
	 */
	public CommandConfigFactoryDTO(String readerFactoryID,
			Set<String> commandConfigTypeIDs) {
		this.readerFactoryID = readerFactoryID;
		this.commandConfigTypeIDs = commandConfigTypeIDs;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * @return the commandConfigFactoryIDs
	 */
	public Set<String> getCommandConfigTypeIDs() {
		return commandConfigTypeIDs;
	}
	
	

}
