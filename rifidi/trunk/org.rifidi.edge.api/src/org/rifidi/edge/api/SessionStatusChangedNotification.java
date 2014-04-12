/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
