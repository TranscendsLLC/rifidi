/**
 * 
 */
package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

/**
 * A notification message that is sent to a client when a
 * CommandConfigurationFactory become unavailable
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CommandConfigFactoryRemoved implements Serializable {

	/** SerialversionID */
	private static final long serialVersionUID = 1L;
	/** The readerFactoryID this CommandConfigurationFactory is associated with */
	private String readerFactoryID;

	/**
	 * @param readerFactoryID
	 */
	public CommandConfigFactoryRemoved(String readerFactoryID) {
		this.readerFactoryID = readerFactoryID;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
}
