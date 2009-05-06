/**
 * 
 */
package org.rifidi.edge.core.readers;
//TODO: Comments
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.api.SessionStatus;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.commands.Command;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class ReaderSession {
	
	/** The ID for this session*/
	private String ID;
	
	public ReaderSession(String ID){
		this.ID = ID;
	}
	
	/**
	 * Open the connection to the readerSession.
	 * 
	 * @throws IOException
	 *             if the connection fails
	 */
	public abstract void connect() throws IOException;

	/**
	 * Close the connection and stop processing of commands.
	 * 
	 */
	public abstract void disconnect();

	/**
	 * Get a map containing all currently executing commands with their process
	 * ids as key.
	 * 
	 * @return
	 */
	public abstract Map<Integer, Command> currentCommands();

	/**
	 * Submit a command for execution.
	 * 
	 * @param command
	 */
	public abstract void submit(Command command);

	/**
	 * Submit a command for execution.
	 * 
	 * @param command
	 */
	public abstract Integer submit(Command command, long interval, TimeUnit unit);

	/**
	 * Kill a command.
	 * 
	 * @param id
	 */
	public abstract void killComand(Integer id);

	/**
	 * Get the status of the readerSession.
	 * 
	 * @return
	 */
	public abstract SessionStatus getStatus();

	/**
	 * 
	 * @return Data Transfer Object for this Session
	 */
	public SessionDTO getDTO() {
		HashMap<Integer, String> commandMap = new HashMap<Integer, String>();
		for (Integer processID : this.currentCommands().keySet()) {
			commandMap.put(processID, this.currentCommands().get(processID)
					.getCommandID());
		}
		return new SessionDTO(this.getID(), this.getStatus(), commandMap);
	}
	
	public String getID(){
		return this.ID;
	}
}
