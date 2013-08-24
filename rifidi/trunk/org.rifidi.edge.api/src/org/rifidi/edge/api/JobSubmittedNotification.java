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
 * A notification that is sent when a command is submitted
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class JobSubmittedNotification implements Serializable {

	/** The serialVersionID */
	private static final long serialVersionUID = 1L;
	/** The reader ID this job is running on */
	private String readerID;
	/** The session ID this job is running on */
	private String sessionID;
	/** The ID of this job */
	private Integer jobID;
	/** The ID of the commandConfiguration of this job */
	private String commandConfigurationID;
	/** True if this is a recurring command */
	private boolean recurring;

	/**
	 * Constructor.
	 * 
	 * @param readerID
	 * @param sessionID
	 * @param jobID
	 * @param commandConfigurationID
	 */
	public JobSubmittedNotification(String readerID, String sessionID,
			Integer jobID, String commandConfigurationID, boolean recurring) {
		this.readerID = readerID;
		this.sessionID = sessionID;
		this.jobID = jobID;
		this.commandConfigurationID = commandConfigurationID;
		this.recurring = recurring;
	}

	/**
	 * Returns the readerID.
	 * 
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Returns the sessionID.
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

	/**
	 * Returns the command configuration ID.
	 * 
	 * @return the commandConfigurationID
	 */
	public String getCommandConfigurationID() {
		return commandConfigurationID;
	}

	/**
	 * @return the recurring
	 */
	public boolean isRecurring() {
		return recurring;
	}
}
