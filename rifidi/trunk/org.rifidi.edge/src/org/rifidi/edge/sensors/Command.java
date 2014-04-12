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
