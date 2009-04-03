/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import org.rifidi.edge.core.api.rmi.dto.CommandConfigurationDTO;

/**
 * @author kyle
 * 
 */
public class RemoteCommandConfiguration {

	private String commandType;
	private String id;
	private String readerFactoryID;

	public RemoteCommandConfiguration(String readerFactoryID,
			CommandConfigurationDTO dto) {
		this.commandType = dto.getCommandConfigType();
		this.id = dto.getCommandConfigID();
		this.readerFactoryID = readerFactoryID;
	}

	public String getCommandType() {
		return commandType;
	}

	public String getID() {
		return id;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
}
