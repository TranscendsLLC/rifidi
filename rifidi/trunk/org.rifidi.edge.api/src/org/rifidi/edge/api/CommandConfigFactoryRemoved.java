/*
 * CommandConfigFactoryRemoved.java
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
 * A notification message that is sent to a client when a
 * CommandConfigurationFactory become unavailable
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigFactoryRemoved implements Serializable {

	/** SerialversionID */
	private static final long serialVersionUID = 1L;
	/** The readerFactoryID this CommandConfigurationFactory is associated with */
	private String readerFactoryID;
	/** The ID command configuraiton factory that was removed */
	private String commandFactoryID;

	/**
	 * @param readerFactoryID
	 */
	public CommandConfigFactoryRemoved(String readerFactoryID,
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
	 * Returns the readerFactoryID for this class.
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
}
