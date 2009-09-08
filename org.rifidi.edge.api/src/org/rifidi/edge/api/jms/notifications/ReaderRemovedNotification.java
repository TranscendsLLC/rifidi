/*
 * ReaderRemovedNotification.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.api.jms.notifications;

import java.io.Serializable;

/**
 * A notification sent to a client when a reader has become unavailable
 * 
 * @author Kyle Neumeier - kyle@pramari.com
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
