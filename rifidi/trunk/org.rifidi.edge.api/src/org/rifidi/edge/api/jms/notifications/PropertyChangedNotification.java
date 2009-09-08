/*
 * PropertyChangedNotification.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.api.jms.notifications;

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
