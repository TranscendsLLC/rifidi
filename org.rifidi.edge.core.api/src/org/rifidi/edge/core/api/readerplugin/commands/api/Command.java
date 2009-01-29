package org.rifidi.edge.core.api.readerplugin.commands.api;

import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.commands.CommandReturnStatus;

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
	 * @param connection
	 *            the Connection to the reader
	 * @param messageQueue
	 *            the MessageQueue for the results
	 * @param errorQueue
	 *            the MessageQueue for the unexpected or error messages
	 * @param configuration
	 *            the XML describing the properties of this command
	 * @param commandID
	 *            the ID this command got assigned
	 * @return the status of the command
	 */
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			CommandConfiguration configuration, long commandID);

	/**
	 * Stop the execution of this command
	 */
	public void stop();
}
