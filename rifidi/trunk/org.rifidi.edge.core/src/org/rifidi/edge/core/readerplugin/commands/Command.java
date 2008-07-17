package org.rifidi.edge.core.readerplugin.commands;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;

/**
 * This is the command Interface defining a common base over all Commands
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface Command {

	/**
	 * Start the execution a of a command
	 * 
	 * @param connection the Connection to the reader
	 * @param messageQueue the MessageQueue for the results
	 * @param configuration the XML describing the properties of this command
	 * @param commandID the ID this command got assigned
	 * @return the status of the command
	 */
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, String configuration, long commandID);

	/**
	 * Stop the execution of this command 
	 */
	public void stop();
}
