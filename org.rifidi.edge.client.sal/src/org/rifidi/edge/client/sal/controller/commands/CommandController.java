
package org.rifidi.edge.client.sal.controller.commands;

import java.util.Set;

import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;


/**
 * TODO: Class level comment.  
 * TODO: More detailed method comments if needed - Matt.  
 * 
 * @author kyle
 */
public interface CommandController {

	/**
	 * Creates a command with the given configuration type.  
	 * 
	 * @param commandConfigType
	 */
	public void createCommand(String commandConfigType);

	/**
	 * Deletes the command with the given ID.  
	 * 
	 * @param commandConfigID
	 */
	public void deleteCommand(String commandConfigID);
	
	/**
	 * Returns the command configurations.  
	 * 
	 * @return
	 */
	public Set<RemoteCommandConfiguration> getCommandConfigurations();
	
	/**
	 * Clears the uncommitted property changes.  
	 * 
	 * @param commandID
	 */
	void clearPropertyChanges(String commandID);
	
	/**
	 * TODO: Method level comment.  
	 * 
	 * @param commandID
	 */
	void synchPropertyChanges(String commandID);
}
