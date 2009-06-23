
package org.rifidi.edge.api.jms.notifications;

import java.io.Serializable;

/**
 * A notification that is sent when a commandconfiguration is removed
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigurationRemovedNotification implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the command Configuration */
	private String commandConfigurationID;

	/**
	 * Constructor
	 * 
	 * @param commandConfigurationID
	 *            The ID of the commandConfig that was removed
	 */
	public CommandConfigurationRemovedNotification(String commandConfigurationID) {
		this.commandConfigurationID = commandConfigurationID;
	}

	/**
	 * Returns the ID for the CommandConfiguration to be removed.  
	 * 
	 * @return the commandConfigurationID
	 */
	public String getCommandConfigurationID() {
		return commandConfigurationID;
	}

}
