package org.rifidi.edge.api.jms.notifications;

import java.io.Serializable;

/**
 * A notification sent to a client when a ReaderFactory has become unavailable
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderFactoryRemovedNotification implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** ReaderFactoryID */
	private String readerFactoryID;

	/**
	 * Constructor.
	 * 
	 * @param readerFactoryID
	 */
	public ReaderFactoryRemovedNotification(String readerFactoryID) {
		this.readerFactoryID = readerFactoryID;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
}
