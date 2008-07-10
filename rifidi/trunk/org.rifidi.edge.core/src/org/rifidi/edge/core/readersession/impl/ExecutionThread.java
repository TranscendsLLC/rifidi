package org.rifidi.edge.core.readersession.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readersession.ReaderSession;

public class ExecutionThread implements Runnable {

	private Log logger = LogFactory.getLog(ExecutionThread.class);
	
	private Thread thread;

	private Connection connection;
	private MessageQueue messageQueue;

	private Command command;
	@SuppressWarnings("unused")
	private CommandReturnStatus status;

	public ExecutionThread(Connection connection, MessageQueue messageQueue,
			ReaderSession readerSession) {
		this.connection = connection;
		this.messageQueue = messageQueue;
		thread = new Thread(this, "ExecutionThread");
		thread.start();
	}

	public void start(Command command) {
		this.command = command;
		synchronized (thread) {
			notify();
		}
	}

	public void stop() {
		command.stop();
		try {
			thread.join(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (thread.isAlive()) {
			thread.interrupt();
		}
	}

	@Override
	public void run() {
		try {
			if (command == null) {
				try {
					status = command.start(connection, messageQueue);
				} catch (Exception e) {
					e.printStackTrace();
					logger.error(
							"Command interrupted due to unexpected Execption. "
									+ "Probibly tried to execute a command that did not belong to this reader, "
									+ "or something else caused it.", e);
				} catch (Error e) {
					e.printStackTrace();
					logger.error(
							"Command interrupted due to unexpected Critical Exception. "
									+ "This may be caused by a bug in the command implemenation itself or, "
									+ "or the protocol implemenation of the reader.",
							e);
				}
				// Wait for the next command
			}
			synchronized (thread) {
				wait();
			}	
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
