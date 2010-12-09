/*
 * SessionAddedNotification.java
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
 * A notification sent to a client when a session has been created
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class SessionAddedNotification implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader */
	private String readerID;
	/** The ID of the session */
	private String sessionID;

	/**
	 * Constructor.
	 * 
	 * @param readerID
	 * @param sessionID
	 */
	public SessionAddedNotification(String readerID, String sessionID) {
		this.readerID = readerID;
		this.sessionID = sessionID;
	}

	/**
	 * Returns the reader ID.
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Returns the session ID.
	 * 
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

}
