
package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

/**
 * TODO: Class level comment. 
 * 
 * @author kyle
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
	 * TODO: Method level comment. 
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}
}
