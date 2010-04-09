/**
 * 
 */
package org.rifidi.edge.core.sensors.commands;

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
