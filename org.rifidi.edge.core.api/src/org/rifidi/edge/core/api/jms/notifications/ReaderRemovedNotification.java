
package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

/**
 * TODO: Class level comment. 
 * 
 * @author kyle
 */
public class ReaderRemovedNotification implements Serializable {
	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;

	/**
	 * Constructor.  
	 * 
	 * @param readerID
	 */
	public ReaderRemovedNotification(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * Returns the ID for the reader.  
	 *  
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

}
