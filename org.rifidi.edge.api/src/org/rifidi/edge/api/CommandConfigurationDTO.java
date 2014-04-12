/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.api;

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
