/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.sensors;


/**
 * An Abstract base class that must be extended by all Reader Commands. Commands
 * are given to the ThreadPoolExecutor in the Reader's Session for execution.
 * The run method in the Commands should execute reasonably quickly and should
 * not be long running.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class Command implements Runnable {
	/** The session this command is executed in. */
	protected SensorSession sensorSession;
	/** The ID of the commandConfiguraiton (the RifidiService) */
	private String commandID;

	/**
	 * Constructor
	 * 
	 * @param commandID
	 *            The ID of the commandconfiguration that produced this
	 *            command(The RifidiService)
	 */
	public Command(String commandID) {
		this.commandID = commandID;
	}

	/**
	 * @return the commandID
	 */
	public String getCommandID() {
		return this.commandID;
	}

	/**
	 * @param sensorSession
	 *            the sensorSession to set
	 */
	public void setReaderSession(SensorSession sensorSession) {
		this.sensorSession = sensorSession;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Command: " + commandID;
	}
	
	

}
