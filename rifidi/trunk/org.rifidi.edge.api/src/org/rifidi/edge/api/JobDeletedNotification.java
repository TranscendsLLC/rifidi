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
