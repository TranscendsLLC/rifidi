/*
 * JobDeletedNotification.java
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
