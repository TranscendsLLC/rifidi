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
