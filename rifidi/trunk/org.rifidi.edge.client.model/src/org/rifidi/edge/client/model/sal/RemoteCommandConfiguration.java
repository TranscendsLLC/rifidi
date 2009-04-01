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

	public RemoteCommandConfiguration(CommandConfigurationDTO dto) {
		this.commandType = dto.getCommandConfigType();
		this.id = dto.getCommandConfigID();
	}

	public String getCommandType() {
		return commandType;
	}

	public String getID() {
		return id;
	}

}
