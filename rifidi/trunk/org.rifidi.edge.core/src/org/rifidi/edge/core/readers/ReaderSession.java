/**
 * 
 */
package org.rifidi.edge.core.readers;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface ReaderSession {
	/**
	 * Open the connection to the readerSession.
	 * 
	 * @throws IOException
	 *             if the connection fails
	 */
	void connect() throws IOException;

	/**
	 * Close the connection and stop processing of commands.
	 * 
	 */
	void disconnect();

	/**
	 * Get a map containing all currently executing commands with their process
	 * ids as key.
	 * 
	 * @return
	 */
	Map<Integer, Command> currentCommands();

	/**
	 * Submit a command for execution.
	 * 
	 * @param command
	 */
	void submit(Command command);

	/**
	 * Submit a command for execution.
	 * 
	 * @param command
	 */
	Integer submit(Command command, long interval, TimeUnit unit);

	/**
	 * Kill a command.
	 * 
	 * @param id
	 */
	void killComand(Integer id);

	/**
	 * Get the status of the readerSession.
	 * 
	 * @return
	 */
	ReaderStatus getStatus();
}
