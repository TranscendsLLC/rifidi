package org.rifidi.edge.core.readersession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiExecutionException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.api.readerplugin.communication.Connection;
import org.rifidi.edge.core.api.readerplugin.messageQueue.EventQueue;

/**
 * This is a Helper to execute Commands. It will take in the command and execute
 * it in it's own thread. It also offers ways to force the stop of a executing
 * command by killing the thread executing the command.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ExecutionThread {

	private Log logger = LogFactory.getLog(ExecutionThread.class);

	private EventQueue messageQueue;

	private EventQueue errorQueue;

	private CommandExecutionListener commandExecutionListener;

	private Thread thread;
	private CommandWrapper commandWrapper;
	private CommandReturnStatus status;

	private boolean running = false;

	/**
	 * Create a new Instance of the Execution Thread.
	 * 
	 * @param connection
	 *            the connection for the commands
	 * @param messageQueue
	 *            the messageQueue for the command
	 * @param readerSession
	 *            the Listener for command finished events
	 */
	public ExecutionThread(EventQueue messageQueue, EventQueue errorQueue,
			CommandExecutionListener readerSession) {
		if (messageQueue == null) {
			logger.error("NO MessageQueue");
		}
		this.messageQueue = messageQueue;

		this.errorQueue = errorQueue;
		if (readerSession == null) {
			logger.error("NO CommandExecutionListerner");
		}
		this.commandExecutionListener = readerSession;
	}

	/**
	 * Start the execution of a command
	 * 
	 * @param _command
	 *            command to execute
	 * @param _configuration
	 *            configuration for the command
	 * @param _commandID
	 *            assigned execution id
	 * @throws RifidiExecutionException
	 *             if the command could not be successful Executed
	 */
	public void start(final Connection connection, final CommandWrapper _command)
			throws RifidiExecutionException {
		if (running || commandWrapper != null) {
			throw new RifidiExecutionException("Command "
					+ this.commandWrapper.getCommandID()
					+ " is still executing");
		}
		if (_command == null) {
			logger.error("NO Command to execute");
			return;
		}
		this.commandWrapper = _command;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.debug("Starting command");
				if (commandWrapper == null) {
					logger.error("NO Command to execute");
					return;
				}
				// TODO: disabled while refactoring
				// status = commandWrapper.getCommand().start(connection,
				// messageQueue, errorQueue,
				// _command.getConfiguration(), commandWrapper.getCommandID());
				// TODO Possibly pass in commandID instead of command
				logger.debug("Command finished");
				commandExecutionListener.commandFinished(commandWrapper
						.getCommand(), status);
				commandWrapper = null;
			}
		}, "ExecuteThread " + commandWrapper.getCommandID());
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * Stop the execution of this command
	 * 
	 * @param force
	 *            force the command to stop by killing the executing thread
	 */
	@SuppressWarnings("deprecation")
	public void stop(boolean force) {
		if (commandWrapper != null) {
			commandWrapper.getCommand().stop();
			if (force) {
				logger.debug("Force shutdown");
				try {
					thread.join(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (thread.isAlive()) {
					thread.interrupt();
					thread.stop();
					commandExecutionListener.commandFinished(commandWrapper
							.getCommand(), CommandReturnStatus.INTERRUPTED);
					commandWrapper = null;
				}
			}
		}
	}

}
