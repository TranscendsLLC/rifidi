/**
 * 
 */
package org.rifidi.edge.newcore.internal;

import java.util.concurrent.Callable;

import org.rifidi.edge.newcore.commands.CommandFactory;
import org.rifidi.edge.newcore.events.EventQueue;
import org.rifidi.edge.newcore.readers.ReaderConfiguration;

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
	void setCommmandFactory(CommandFactory<?> command);

	/**
	 * Get the command registered to the session. May be null.
	 */
	CommandFactory<?> getCommandFactory();

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
