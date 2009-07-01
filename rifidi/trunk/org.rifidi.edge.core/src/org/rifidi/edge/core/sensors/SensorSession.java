package org.rifidi.edge.core.sensors;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.Command;

/**
 * This class represents a session with a reader.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public abstract class SensorSession {

	/** The ID for this session */
	private String ID;
	/** The sensor this session is associated with. */
	protected AbstractSensor<?> sensor;
	/**
	 * Constructor.
	 * 
	 * @param ID
	 * @param sensor
	 */
	public SensorSession(String ID, AbstractSensor<?> sensor) {
		this.ID = ID;
		this.sensor=sensor;
	}

	/**
	 * Open the connection to the sensorSession.
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
	 * Get the status of the sensorSession.
	 * 
	 * @return The status of the SensorSession
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

	/**
	 * @return the sensor
	 */
	public AbstractSensor<?> getSensor() {
		return sensor;
	}
	
}
