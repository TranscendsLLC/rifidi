/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api;

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
