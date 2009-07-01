package org.rifidi.edge.core.sensors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.sensors.commands.Command;

/**
 * This class represents a session with a reader.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public abstract class ReaderSession {

	/** The ID for this session */
	private String ID;

	/**
	 * Constructor.
	 * 
	 * @param ID
	 */
	public ReaderSession(String ID) {
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
	 * Submit a command for a one-time execution.
	 * 
	 * @param command
	 *            The command to execute
	 */
	public abstract void submit(Command command);

	/**
	 * Schedule a command to a reader session for repeated execution.
	 * 
	 * @param command
	 *            The command to execute
	 * @param interval
	 *            The interval between executions
	 * @param unit
	 *            The Unit of time to measure the interval
	 * @return The ID of the job
	 */
	public abstract Integer submit(Command command, long interval, TimeUnit unit);

	/**
	 * Kill a command.
	 * 
	 * @param id
	 *            The ID of the command to kill
	 */
	public abstract void killComand(Integer id);

	/**
	 * Get the status of the readerSession.
	 * 
	 * @return The status of the ReaderSession
	 */
	public abstract SessionStatus getStatus();

	/**
	 * Get the Data Transfer Object used to serialize a Reader Session
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

	/**
	 * Returns the ID for this session.
	 * 
	 * @return the ID of the session
	 */
	public String getID() {
		return this.ID;
	}
}
