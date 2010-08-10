/*
 * 
 * Command.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.core.sensors.commands;

import org.rifidi.edge.core.sensors.SensorSession;

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
