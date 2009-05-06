package org.rifidi.edge.core.api.jms.notifications;
//TODO: Comments
import java.io.Serializable;

public class ReaderRemovedNotification implements Serializable {
	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;

	/**
	 * @param readerID
	 */
	public ReaderRemovedNotification(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

}
