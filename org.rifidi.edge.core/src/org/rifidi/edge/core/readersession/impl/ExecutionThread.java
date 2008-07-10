package org.rifidi.edge.core.readersession.impl;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readersession.ReaderSession;

public class ExecutionThread implements Runnable {

	private Thread thread;

	private Connection connection;
	private MessageQueue messageQueue;

	private Command command;
	@SuppressWarnings("unused")
	private CommandStatus status;

	public ExecutionThread(Connection connection, MessageQueue messageQueue,
			ReaderSession readerSession) {
		this.connection = connection;
		this.messageQueue = messageQueue;
		thread = new Thread(this, "ExecutionThread");
		thread.start();
	}

	public void start(Command command, CommandStatus status) {
		this.command = command;
		this.status = status;
		synchronized (this) {
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
			try {
				status = command.start(connection, messageQueue);
			} catch (Exception e) {
				e.printStackTrace();
			} catch (Error e) {
				e.printStackTrace();
			}
			// Wait for the next command

			wait();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
