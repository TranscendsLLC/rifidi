/**
 * 
 */
package org.rifidi.edge.newcore.readers;

import java.io.IOException;
import java.util.concurrent.Future;

import org.rifidi.edge.newcore.commands.Command;
import org.rifidi.edge.newcore.commands.CommandState;

/**
 * A reader represents two things: <br/>
 * 1. An open connection to a physical reader<br/>
 * 2. A state of this reader (current antenna sequence, attenuation, ...)<br/>
 * This object is immutable after it is created!
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface Reader {

	/**
	 * Send the Message to the reader
	 * 
	 * 
	 * @param o
	 *            Message(in the Format of the specific reader) to send
	 * @throws IOException
	 *             if there was a problem in the underlying communication layer
	 */
	// TODO: would be better if the Object is actually a Message
	void sendMessage(Object o) throws IOException;

	/**
	 * Recieve a Message form the reader
	 * 
	 * @return the Message(in the Format of the specific reader) read form the
	 *         reader
	 * @throws IOException
	 *             if there was a problem in the underlying communication layer
	 */
	Object receiveMessage() throws IOException;

	/**
	 * @return
	 * @throws IOException
	 */
	boolean isMessageAvailable() throws IOException;

	/**
	 * Receive a message
	 * 
	 * @param timeout
	 *            Time out in milliseconds.
	 * @return Object if successful, null if unsuccessful
	 * @throws IOException
	 */
	Object receiveMessage(long timeout) throws IOException;

	/**
	 * Submit a command for execution.
	 * 
	 * @param command
	 * @return
	 */
	Future<CommandState> execute(Command command);

	/**
	 * Bring doen the reader and commands executing in it as fast as possible.
	 */
	void terminate();
}
