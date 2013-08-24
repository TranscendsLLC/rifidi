/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.api;

import java.io.Serializable;

/**
 * A notification message that is sent to a client when a
 * CommandConfigurationFactory becomes available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigFactoryAdded implements Serializable {
	/** SerialversionID */
	private static final long serialVersionUID = 1L;
	/** The readerFactoryID this CommandConfigurationFactory is associated with */
	private String readerFactoryID;
	/** The ID of the commandFactory */
	private String commandFactoryID;

	/**
	 * Constructor
	 * 
	 * @param readerFactoryID
	 *            The ID of the factory that was added
	 */
	public CommandConfigFactoryAdded(String readerFactoryID,
			String commandFactoryID) {
		this.readerFactoryID = readerFactoryID;
		this.commandFactoryID = commandFactoryID;
	}

	/**
	 * @return the commandFactoryID
	 */
	public String getCommandFactoryID() {
		return commandFactoryID;
	}

	/**
	 * @return The ID of the factory that was added
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

}
