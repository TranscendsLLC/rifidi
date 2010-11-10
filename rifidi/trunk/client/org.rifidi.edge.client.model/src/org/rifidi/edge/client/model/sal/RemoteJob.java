/*
 * RemoteJob.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.client.model.sal;

/**
 * This represents a command that has been submitted to a session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteJob {

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
	 * @param readerID
	 * @param sessionID
	 * @param jobID
	 * @param commandConfigurationID
	 * @param recurring
	 */
	public RemoteJob(String readerID, String sessionID, Integer jobID,
			String commandConfigurationID, boolean recurring) {
		this.readerID = readerID;
		this.sessionID = sessionID;
		this.jobID = jobID;
		this.commandConfigurationID = commandConfigurationID;
		this.recurring = recurring;
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @return the sessionID
	 */
	public String getSessionID() {
		return sessionID;
	}

	/**
	 * @return the jobID
	 */
	public Integer getJobID() {
		return jobID;
	}

	/**
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
