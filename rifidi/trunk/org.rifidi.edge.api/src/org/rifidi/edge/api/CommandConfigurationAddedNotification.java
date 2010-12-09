/*
 * CommandConfigurationAddedNotification.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.api;

import java.io.Serializable;

/**
 * A notification that is sent whenever a CommandConfiguration becomes available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigurationAddedNotification implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the command Configuration */
	private String commandConfigurationID;

	/**
	 * Constructor
	 * 
	 * @param commandConfigurationID
	 *            The ID of the commandConfig that was added
	 */
	public CommandConfigurationAddedNotification(String commandConfigurationID) {
		this.commandConfigurationID = commandConfigurationID;
	}

	/**
	 * Returns the commandConfigurationID for this notification.  
	 * 
	 * @return the commandConfigurationID
	 */
	public String getCommandConfigurationID() {
		return commandConfigurationID;
	}
}
