/*
 * CommandConfigurationDTO.java
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

import javax.management.AttributeList;

/**
 * A Data Transfer Object that is used to send a CommandConfiguration to a
 * client
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigurationDTO implements Serializable {

	/** Default SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the command configuration */
	private String commandConfigID;
	/** The type of the command configuration */
	private String commandConfigFactoryID;
	/** The list of attributes of the commandConfiguration */
	private AttributeList attributes;

	/**
	 * Constructor.
	 * 
	 * @param commandConfigID
	 *            The ID of the CommandConfiguration
	 * @param commandConfigFactoryID
	 *            The type of the command configuration
	 * @param readerFactoryID
	 *            The ID of the reader factory this command works with
	 */
	public CommandConfigurationDTO(String commandConfigID,
			String commandConfigFactoryID, AttributeList attributes) {
		super();
		this.commandConfigID = commandConfigID;
		this.commandConfigFactoryID = commandConfigFactoryID;
		this.attributes = attributes;
	}

	/**
	 * Returns the ID for this CommandConfiguration.
	 * 
	 * @return the commandConfigID
	 */
	public String getCommandConfigID() {
		return commandConfigID;
	}

	/**
	 * Returns the ID of the CommandFactory that created this command.
	 * 
	 * @return the commandConfigType
	 */
	public String getCommandConfigFactoryID() {
		return commandConfigFactoryID;
	}

	/**
	 * Returns the AttributeList for this CommandConfiguration.
	 * 
	 * @return the attributes
	 */
	public AttributeList getAttributes() {
		return attributes;
	}
}
