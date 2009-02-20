/**
 * 
 */
package org.rifidi.edge.core.internal;

import java.util.concurrent.Callable;

import org.rifidi.edge.core.commands.CommandConfiguration;
import org.rifidi.edge.core.events.EventQueue;
import org.rifidi.edge.core.readers.ReaderConfiguration;

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
	void setReaderFactory(ReaderConfiguration<?> readerFactory);

	/**
	 * Set the eventQueue for outgoing messages.
	 * 
	 * @param eventQueue
	 */
	void setEventQueue(EventQueue eventQueue);

	/**
	 * Get the reader factory currently used by the session.
	 * 
	 * @return
	 */
	ReaderConfiguration<?> getReaderFactory();

	/**
	 * Set the command to be executed.
	 * 
	 * @param command
	 */
	void setCommmandFactory(CommandConfiguration<?> command);

	/**
	 * Get the command registered to the session. May be null.
	 */
	CommandConfiguration<?> getCommandFactory();

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
