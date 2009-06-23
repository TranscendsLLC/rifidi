
package org.rifidi.edge.api.jms.notifications;

import java.io.Serializable;

/**
 * Notification for when a reader is added.  
 * 
 * @author kyle
 */
public class ReaderAddedNotification implements Serializable{
	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;

	/**
	 * Constructor.  
	 * 
	 * @param readerID
	 */
	public ReaderAddedNotification(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * Returns the readerID.  
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

}
