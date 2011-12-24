/*
 * ReaderFactoryAddedNotification.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.api;

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
