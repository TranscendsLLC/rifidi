package org.rifidi.edge.core.readersession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;

public class ExecutionThread implements Runnable {

	private Log logger = LogFactory.getLog(ExecutionThread.class);

	private Thread thread;

	private Connection connection;
	private MessageQueue messageQueue;

	private Command command;
	@SuppressWarnings("unused")
	private CommandReturnStatus status;

	private CommandExecutionListener readerSession;

	private boolean running = true;

	private long commandID;

	public ExecutionThread(Connection connection, MessageQueue messageQueue,
			CommandExecutionListener readerSession) {
		this.connection = connection;
		this.messageQueue = messageQueue;
		this.readerSession = readerSession;
		thread = new Thread(this, "ExecutionThread: " + messageQueue.getName());
		thread.start();
	}

	public void start(Command command, long commandID) {
		// TODO think if this should be really a global variable
		this.commandID = commandID;
		this.command = command;

		synchronized (this) {
			notify();
		}
	}

	public void stop() {
		running = false;
		command.stop();
		try {
			thread.join(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (thread.isAlive()) {
			thread.interrupt();
		}
		command = null;
	}

	@Override
	public void run() {
		while (running)
			try {
				if (command != null) {
					try {
						logger.debug("starting command");
						// TODO define config.xml for commands
						status = command.start(connection, messageQueue, "",
								commandID);
					} catch (Exception e) {
						e.printStackTrace();
						logger
								.error(
										"Command interrupted due to unexpected Execption. "
												+ "Probibly tried to execute a command that did not belong to this reader, "
												+ "or something else caused it.",
										e);
					}
					// Wait for the next command
				}
				readerSession.commandFinished(command, status);
				synchronized (this) {
					wait();
				}
			} catch (InterruptedException e) {
				// TODO well not sure if we should print that
				e.printStackTrace();
			}
	}
}
