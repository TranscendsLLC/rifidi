/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
