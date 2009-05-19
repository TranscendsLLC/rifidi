package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

/**
 * A notification sent to a client when a ReaderFactory has become available
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReaderFactoryAddedNotification implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** ReaderFactoryID */
	private String readerFactoryID;

	/**
	 * Constructor.
	 * 
	 * @param readerFactoryID
	 */
	public ReaderFactoryAddedNotification(String readerFactoryID) {
		super();
		this.readerFactoryID = readerFactoryID;
	}

	/**
	 * Returns the ID for this readerfactory.
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

}
