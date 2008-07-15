package org.rifidi.edge.core.readersession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiExecutionException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;

/**
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ExecutionThread {

	private Log logger = LogFactory.getLog(ExecutionThread.class);

	private Connection connection;
	private MessageQueue messageQueue;

	private CommandExecutionListener readerSession;

	private Thread thread;
	private Command command;
	private long commandID;
	private CommandReturnStatus status;

	private boolean running = false;

	/**
	 * @param connection
	 * @param messageQueue
	 * @param readerSession
	 */
	public ExecutionThread(Connection connection, MessageQueue messageQueue,
			CommandExecutionListener readerSession) {
		this.connection = connection;
		this.messageQueue = messageQueue;
		this.readerSession = readerSession;
	}

	/**
	 * @param command
	 * @param commandID
	 */
	public void start(final Command _command, final String _configuration,
			final long _commandID) throws RifidiExecutionException {
		if (running || thread.isAlive()) {
			logger.error("command " + this.commandID + "is still executing");
			throw new RifidiExecutionException("Command " + this.commandID
					+ " is still executing");
		}
		this.command = _command;
		this.commandID = _commandID;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				status = command.start(connection, messageQueue,
						_configuration, commandID);
				readerSession.commandFinished(command, status);
				command = null;
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

	/**
	 * @param force
	 */
	@SuppressWarnings("deprecation")
	public void stop(boolean force) {
		if (command != null) {
			command.stop();
			if (force) {
				try {
					thread.join(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (thread.isAlive()) {
					thread.interrupt();
					thread.stop();
				}
			}
		}
	}

}
