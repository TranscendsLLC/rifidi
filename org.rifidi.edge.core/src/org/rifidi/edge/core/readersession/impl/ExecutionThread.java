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

	private CommandExecutionListener commandExecutionListener;

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
		if(connection == null)
		{
			logger.error("NO Connection");
		}
		this.connection = connection;
		if(messageQueue == null)
		{
			logger.error("NO MessageQueue");
		}
		this.messageQueue = messageQueue;
		if(commandExecutionListener == null)
		{
			logger.error("NO CommandExecutionListerner");
		}
		this.commandExecutionListener = readerSession;
	}

	/**
	 * @param command
	 * @param commandID
	 */
	public void start(final Command _command, final String _configuration,
			final long _commandID) throws RifidiExecutionException {
		if (running || command != null){
			logger.error("command " + this.commandID + "is still executing");
			throw new RifidiExecutionException("Command " + this.commandID
					+ " is still executing");
		}
		if(_command == null)
		{
			logger.error("NO Command to execute");
			return;
		}
		this.command = _command;
		this.commandID = _commandID;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				logger.debug("Starting command");
				if(command == null)
				{
					logger.error("NO Command to execute");
					return;
				}
				status = command.start(connection, messageQueue,
						_configuration, commandID);
				commandExecutionListener.commandFinished(command, status);
				command = null;
				logger.debug("Command finished");
			}
		},"ExecuteThread "  + commandID);
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
				logger.debug("Force shutdown");
				try {
					thread.join(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (thread.isAlive()) {
					thread.interrupt();
					thread.stop();
					commandExecutionListener.commandFinished(command, CommandReturnStatus.INTERRUPTED);
					command = null;
				}
			}
		}
	}

}
