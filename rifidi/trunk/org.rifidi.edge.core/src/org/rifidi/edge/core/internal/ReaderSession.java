/**
 * 
 */
package org.rifidi.edge.core.internal;

import java.util.concurrent.Callable;

import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.readers.AbstractReaderConfiguration;

/**
 * TODO: This interface should extend RifidiService in order to persist reader
 * sessions
 * 
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
	void setReaderFactory(AbstractReaderConfiguration<?> readerFactory);

	/**
	 * Get the reader factory currently used by the session.
	 * 
	 * @return
	 */
	AbstractReaderConfiguration<?> getReaderFactory();

	/**
	 * Set the command to be executed.
	 * 
	 * @param command
	 */
	void setCommmandFactory(AbstractCommandConfiguration<?> command);

	/**
	 * Get the command registered to the session. May be null.
	 */
	AbstractCommandConfiguration<?> getCommandFactory();

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

	/**
	 * TODO: This method will go away once this interface extends RifidiService
	 * Get the ID associated with this session
	 * 
	 * @param id
	 */
	void setID(String id);

	/**
	 * TODO: This method will go away once this interface extends RifidiService
	 * 
	 * @return The ID associated with this session
	 */
	String getID();

}
