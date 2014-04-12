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
 * This notification is sent from the server to the UI when properties have
 * changed (either on a reader or a command)
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class PropertyChangedNotification implements Serializable {

	/** default serialversion ID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader or the command */
	private String configID;
	/** The attributes that have changed */
	private AttributeList attributes;
	/**
	 * Constructor.
	 * 
	 * @param configurationID
	 *            The ID of the configuration that has the attributes
	 * @param attribute
	 *            The attributes that have changed
	 * @param isReader
	 *            true if the configuration is a reader, false if it is a
	 *            CommandConfiguration
	 */
	public PropertyChangedNotification(String configurationID,
			AttributeList attributes) {
		super();
		this.configID = configurationID;
		this.attributes = attributes;
	}

	/**
	 * Returns the ID of the configuration
	 * 
	 * @return the configID
	 */
	public String getConfigID() {
		return configID;
	}

	/**
	 * Returns the attributes for these properties.
	 * 
	 * @return the attributes
	 */
	public AttributeList getAttributes() {
		return attributes;
	}

}
