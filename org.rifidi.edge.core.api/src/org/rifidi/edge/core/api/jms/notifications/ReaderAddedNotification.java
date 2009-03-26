package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

public class ReaderAddedNotification implements Serializable{
	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;

	/**
	 * @param readerID
	 */
	public ReaderAddedNotification(String readerID) {
		this.readerID = readerID;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

}
