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
 * A notification sent to a client when the status of a session changes.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SessionStatusChangedNotification implements Serializable {

	/** The SerialVersion ID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader this session belongs to */
	private String readerID;
	/** The ID of the session */
	private String sessionID;
	/** The new status for the session */
	private SessionStatus status;

	/**
	 * Constructor.  
	 * 
	 * @param readerID
	 *            The ID of the reader that this session belongs to
	 * @param sessionID
	 *            The ID of the session
	 * @param status
	 *            The new status of the session
	 */
	public SessionStatusChangedNotification(String readerID, String sessionID,
			SessionStatus status) {
		super();
		this.readerID = readerID;
		this.sessionID = sessionID;
		this.status = status;
	}

	/**
	 * Returns the ID for the reader.  
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Returns the ID for the session.  
	 * 
	 * @return the sessioID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * Returns the status of the session.  
	 * 
	 * @return the status
	 */
	public SessionStatus getStatus() {
		return status;
	}

}
