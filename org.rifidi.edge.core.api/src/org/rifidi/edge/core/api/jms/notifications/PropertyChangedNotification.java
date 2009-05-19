package org.rifidi.edge.core.api.jms.notifications;

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
	/** True if the configuration is a reader, false if it is a command */
	private boolean isReader;

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
			AttributeList attributes, boolean isReader) {
		super();
		this.configID = configurationID;
		this.attributes = attributes;
		this.isReader = isReader;
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

	/**
	 * Is the PropertyChangeNotification for a reader?
	 * 
	 * @return true if the configuration is for a reader. False if it is for a
	 *         command
	 */
	public boolean isReader() {
		return isReader;
	}

}
