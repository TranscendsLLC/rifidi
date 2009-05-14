package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

/**
 * TODO: Class level comment. 
 * 
 * @author Matthew Dean
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
