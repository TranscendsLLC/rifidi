package org.rifidi.edge.core.readersession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiExecutionException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.w3c.dom.Document;

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

	private MessageQueue messageQueue;
	
	private MessageQueue errorQueue;

	private CommandExecutionListener commandExecutionListener;

	private Thread thread;
	private Command command;
	private long commandID;
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
	public ExecutionThread( MessageQueue messageQueue, MessageQueue errorQueue,
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
	public void start(final Connection connection, final Command _command, final Document _configuration,
			final long _commandID) throws RifidiExecutionException {
		if (running || command != null) {
			logger.error("command " + this.commandID + "is still executing");
			throw new RifidiExecutionException("Command " + this.commandID
					+ " is still executing");
		}
		if (_command == null) {
			logger.error("NO Command to execute");
			return;
		}
		this.command = _command;
		this.commandID = _commandID;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.debug("Starting command");
				if (command == null) {
					logger.error("NO Command to execute");
					return;
				}
				status = command.start(connection, messageQueue, errorQueue,
						_configuration, commandID);
				// TODO Possibly pass in commandID instead of command
				commandExecutionListener.commandFinished(command, status);
				command = null;
				logger.debug("Command finished");
			}
		}, "ExecuteThread " + commandID);
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
		if (command != null) {
			command.stop();
			if (force) {
				logger.debug("Force shutdown");
				try {
					logger.debug("before join");
					thread.join(5000);
					logger.debug("after join");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (thread.isAlive()) {
					thread.interrupt();
					thread.stop();
					commandExecutionListener.commandFinished(command,
							CommandReturnStatus.INTERRUPTED);
					command = null;
				}
			}
		}
	}

}
