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
