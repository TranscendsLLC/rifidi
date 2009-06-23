/**
 * 
 */
package org.rifidi.edge.api.jms.notifications;

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

	/**
	 * Constructor
	 * 
	 * @param readerFactoryID
	 *            The ID of the factory that was added
	 */
	public CommandConfigFactoryAdded(String readerFactoryID) {
		this.readerFactoryID = readerFactoryID;
	}

	/**
	 * @return The ID of the factory that was added
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

}
