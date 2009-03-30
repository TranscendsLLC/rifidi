/**
 * 
 */
package org.rifidi.edge.core.api.rmi.dto;

import java.util.Set;

/**
 * @author kyle
 *
 */
public class CommandConfigPluginDTO {
	
	private String readerFactoryID;
	
	private Set<String> commandConfigFactoryIDs;

	/**
	 * @param readerFactoryID
	 * @param commandConfigFactoryIDs
	 */
	public CommandConfigPluginDTO(String readerFactoryID,
			Set<String> commandConfigFactoryIDs) {
		this.readerFactoryID = readerFactoryID;
		this.commandConfigFactoryIDs = commandConfigFactoryIDs;
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
	public Set<String> getCommandConfigFactoryIDs() {
		return commandConfigFactoryIDs;
	}
	
	

}
