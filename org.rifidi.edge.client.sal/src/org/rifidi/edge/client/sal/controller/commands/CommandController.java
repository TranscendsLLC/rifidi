/**
 * 
 */
package org.rifidi.edge.client.sal.controller.commands;

import java.util.Set;

import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;


/**
 * @author kyle
 * 
 */
public interface CommandController {

	public void createCommand(String commandConfigType);

	public void deleteCommand(String commandConfigID);
	
	public Set<RemoteCommandConfiguration> getCommandConfigurations();
	
	void clearPropertyChanges(String commandID);
	
	void synchPropertyChanges(String commandID);
}
