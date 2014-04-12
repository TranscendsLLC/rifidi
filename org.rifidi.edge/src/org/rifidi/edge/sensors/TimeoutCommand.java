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
package org.rifidi.edge.sensors;

import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An Abstract base class that can be used by Reader Commands which timeout.
 * Instead of implementing the run() method of the Command class, concrete
 * implementations implement the execute method that throws a TimeoutException.
 * If the TimeoutException is thrown, this class will call the handle session's
 * timeout method.
 * 
 * To determine how long to wait on a response before throwing a
 * TimeoutException, the execute method should use the system property
 * 'org.rifidi.edge.sessions.timeout'
 * 
 * The execute method in the Commands should execute reasonably quickly and
 * should not be long running.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class TimeoutCommand extends Command {

	private static final Log logger = LogFactory.getLog(TimeoutCommand.class);

	/**
	 * Constructor
	 * 
	 * @param commandID
	 *            The ID of the command
	 */
	public TimeoutCommand(String commandID) {
		super(commandID);
	}

	/**
	 * Implementations should override this command to do the work of
	 * communicating with a sensor
	 * 
	 * @throws TimeoutException
	 *             If the command timed out.
	 */
	protected abstract void execute() throws TimeoutException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			execute();
		} catch (TimeoutException e) {
			logger.error("Timeout Exception on " + sensorSession.getSensor()
					+ ":" + sensorSession + " " + this);
			super.sensorSession.handleTimeout();
		}

	}

}
