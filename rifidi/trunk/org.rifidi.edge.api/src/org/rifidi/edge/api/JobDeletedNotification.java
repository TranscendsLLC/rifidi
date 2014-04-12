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
 * A Notification that is sent when a Job is deleted from a session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class JobDeletedNotification implements Serializable {

	/** The serialVersionID */
	private static final long serialVersionUID = 1L;
	/** The reader ID this job is running on */
	private String readerID;
	/** The session ID this job is running on */
	private String sessionID;
	/** The ID of this job */
	private Integer jobID;

	/**
	 * Constructor.   
	 * 
	 * @param readerID
	 * @param sessionID
	 * @param jobID
	 */
	public JobDeletedNotification(String readerID, String sessionID,
			Integer jobID) {
		this.readerID = readerID;
		this.sessionID = sessionID;
		this.jobID = jobID;
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
	 * Returns the session ID.  
	 * 
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * Returns the jobID.
	 *  
	 * @return the jobID
	 */
	public Integer getJobID() {
		return jobID;
	}

}
