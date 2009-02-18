package org.rifidi.edge.newcore.commands;

import java.util.concurrent.Callable;

import org.rifidi.edge.core.api.readerplugin.messageQueue.MessageQueue;
import org.rifidi.edge.newcore.readers.Reader;

/**
 * This is the command Interface defining a common base over all Commands
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class Command implements
		Callable<CommandState> {

	private Reader reader;
	private MessageQueue messageQueue;


	/**
	 * @param reader the reader to set
	 */
	public void setReader(Reader reader) {
		this.reader = reader;
	}

	/**
	 * @param messageQueue the messageQueue to set
	 */
	public void setMessageQueue(MessageQueue messageQueue) {
		this.messageQueue = messageQueue;
	}

	/**
	 * Return the reader the command is running on.
	 * 
	 * @return
	 */
	public Reader getReader() {
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