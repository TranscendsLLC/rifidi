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
