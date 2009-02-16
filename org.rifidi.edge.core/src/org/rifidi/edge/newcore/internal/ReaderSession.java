/**
 * 
 */
package org.rifidi.edge.newcore.internal;

import java.util.concurrent.Callable;

import org.rifidi.edge.core.api.readerplugin.messageQueue.MessageQueue;
import org.rifidi.edge.newcore.Command;
import org.rifidi.edge.newcore.ReaderConfiguration;

/**
 * A reader session manages the lifecycle of commands.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ReaderSession extends Callable<Boolean> {
	/**
	 * Set the reader factory to be use.
	 * 
	 * @param readerFactory
	 */
	void setReaderFactory(ReaderConfiguration readerFactory);

	/**
	 * Set the message queue for outgoing messages.
	 * 
	 * @param messageQueue
	 */
	void setMessageQueue(MessageQueue messageQueue);

	/**
	 * Get the reader factory currently used by the session.
	 * 
	 * @return
	 */
	ReaderConfiguration getReaderFactory();

	/**
	 * Set the command to be executed.
	 * 
	 * @param command
	 */
	void setCommmand(Command command);

	/**
	 * Get the command registered to the session. May be null.
	 */
	Command getCommand();

	/**
	 * Stop the current command.
	 */
	void stop();

	/**
	 * Destroy the service.
	 */
	void destroy();

	/**
	 * Check if the session is currently executing a command.
	 * 
	 * @return
	 */
	boolean isRunning();

}
