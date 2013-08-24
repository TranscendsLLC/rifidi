/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api;

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
