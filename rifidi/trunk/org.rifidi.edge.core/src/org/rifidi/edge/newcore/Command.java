package org.rifidi.edge.newcore;

import java.util.concurrent.Callable;

import org.rifidi.edge.core.api.readerplugin.messageQueue.MessageQueue;

/**
 * This is the command Interface defining a common base over all Commands
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class Command<T extends Reader> implements
		Callable<CommandState> {

	private T reader;
	private MessageQueue messageQueue;

	/**
	 * @param reader
	 * @param messageQueue
	 */
	public Command(T reader, MessageQueue messageQueue) {
		super();
		this.reader = reader;
		this.messageQueue = messageQueue;
	}

	/**
	 * Return the reader the command is running on.
	 * 
	 * @return
	 */
	public T getReader() {
		return reader;
	}

	/**
	 * Return the message queue for the command.
	 * 
	 * @return
	 */
	public MessageQueue getMessageQueue() {
		return messageQueue;
	}

	/**
	 * Stop the execution of this command
	 */
	public abstract void stop();

}