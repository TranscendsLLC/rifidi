/*
 * CommandConfigFactoryDTO.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.api.rmi.dto;

import java.io.Serializable;

/**
 * A Data Transfer Object for CommandConfigurationFactories. For serializing
 * information about a CommandConfiguraitonFactoy
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigFactoryDTO implements Serializable {

	/** The default serial ID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader factory that this command config works with */
	private String readerFactoryID;
	/** The commandconfiguration id this factory produces */
	private String commandFactoryID;

	/**
	 * Constructor
	 * 
	 * @param readerFactoryID
	 *            The ID of the readerfactory that this command configuration
	 *            factory works with
	 * @param commandFactoryID
	 *            The command configuration id this factory produces
	 */
	public CommandConfigFactoryDTO(String readerFactoryID,
			String commandFactoryID) {
		this.readerFactoryID = readerFactoryID;
		this.commandFactoryID = commandFactoryID;
	}

	/**
	 * Returns the ReaderFactoryID for this class.
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * Returns the command configuration id this factory produces.
	 * 
	 * @return the commandConfigFactoryID
	 */
	public String getCommandFactoryID() {
		return commandFactoryID;
	}

}