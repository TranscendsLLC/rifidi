/*
 * 
 * SensorSession.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.sensors;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.rmi.dto.CommandDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.Command;

/**
 * This class represents a session with a sensor, which is an active connection
 * (TCP, serial, etc) to a physical sensor. The Session is immutable once it has
 * been created in terms of the address of the sensor.
 * 
 * There are two basic types of commands that can be submitted to sessions:
 * Recurring and single-shot. Recurring commands are scheduled for repeated
 * execution according to some time interval, and single-shot commands execute
 * only once.
 * 
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class SensorSession {

	/** The ID for this session */
	private final String ID;
	/** The sensor this session is associated with. */
	protected final AbstractSensor<?> sensor;
	/** Synchronized List of Submitted Commands */
	protected final List<CommandDTO> commands;

	/**
	 * Constructor.
	 * 
	 * @param ID
	 *            The ID of the session
	 * @param sensor
	 *            The Sensor this session belongs to
	 */
	public SensorSession(String ID, AbstractSensor<?> sensor) {
		this.ID = ID;
		this.sensor = sensor;
		this.commands = Collections
				.synchronizedList(new LinkedList<CommandDTO>());
	}

	/**
	 * Open the connection to the Sensor.
	 * 
	 * @throws IOException
	 *             if the connection fails
	 */
	public void connect() throws IOException {
		_connect();
		submitQueuedCommands();
	}

	/**
	 * A method for subclasses to overwrite to actually do the work of
	 * connecting.
	 * 
	 * @throws IOException
	 */
	protected abstract void _connect() throws IOException;

	/**
	 * This method returns a command that is used to reset the reader to a known
	 * state. This command will execute after the reader has successfully
	 * connected.
	 * 
	 * @return
	 */
	protected abstract Command getResetCommand();

	/**
	 * This method executes all commands that have been submitted while the
	 * session was not in the processing state and thus could not be submitted
	 * to the executor.
	 */
	protected abstract void submitQueuedCommands();

	/**
	 * Close the connection and stop processing of commands.
	 */
	public abstract void disconnect();

	/**
	 * Get a list of all commands in their execution order
	 * 
	 * @return
	 */
	public List<CommandDTO> getCommands() {
		return new LinkedList<CommandDTO>(this.commands);
	}

	/**
	 * Submit a command to a reader session for execution. If interval > 0, the
	 * command will be scheduled for repeated execution.
	 * 
	 * @param commandID
	 *            The command to execute
	 * @param interval
	 *            The interval between executions
	 * @param unit
	 *            The Unit of time to measure the interval
	 * @return The ID of the job
	 */
	public abstract Integer submit(String commandID, long interval,
			TimeUnit unit);

	/**
	 * Submit a command for a one-time execution. This method is intended to be
	 * used for internal commands (i.e. commands that do not have a
	 * configuration and thus are not registered in OSGi). Commands submitted
	 * using this method cannot be persisted, and will not restart automatically
	 * if the session stops.
	 * 
	 * @param command
	 *            The command to execute
	 */
	public abstract void submit(Command command);

	/**
	 * Schedule a command for a repeated execution. This method is intended to
	 * be used for internal commands (i.e. commands that do not have a
	 * configuration and thus are not registered in OSGi). Commands submitted
	 * using this method cannot be persisted, and will not restart automatically
	 * if the session stops.
	 * 
	 * @param command
	 *            The command to execute
	 */
	public abstract void submit(Command command, long interval, TimeUnit unit);

	/**
	 * Submit a command for a one-time execution. This method is intended to be
	 * used for internal commands (i.e. commands that do not have a
	 * configuration and thus are not registered in OSGi). Commands submitted
	 * using this method cannot be persisted, and will not restart automatically
	 * if the session stops.
	 * 
	 * This method will block until the command has finished its execution or
	 * will throw a TimeoutException if the given amount of time has expired
	 * before the command finished.
	 * 
	 * @param command
	 *            The command to execute
	 * @param timeout
	 *            The amount of time to wait. If -1, wait forever before
	 *            throwing a TimoutException
	 * @param TimeUnit
	 *            The unit of time used with timeout
	 * @return true if the command returned. False if the command was not
	 *         submitted or did not finish executing
	 * 
	 */
	public abstract boolean submitAndBlock(Command command, long timeout,
			TimeUnit unit);

	/**
	 * This method deletes a command from the session, including the DTO. If the
	 * command is recurring, it will unschedule it. This command will not
	 * execute the next time the session starts up
	 * 
	 * @param id
	 *            The ID of the command to kill
	 */
	public abstract void killComand(Integer id);

	/**
	 * Kill all the commands that have been submitted to this session.
	 */
	public void killAllCommands() {
		List<CommandDTO> dtos = new LinkedList<CommandDTO>(commands);
		for (CommandDTO dto : dtos) {
			killComand(dto.getProcessID());
		}
	}

	/**
	 * This method should be called by subclasses to restore the commands that
	 * need to be submitted to a session from a SessionDTO. It should normally
	 * be called from the createSensorSession(SessionDTO) method
	 * 
	 * @param dto
	 *            The DTO of the session which contains the DTOs of the commands
	 */
	public void restoreCommands(SessionDTO dto) {
		if (dto.getCommands() != null) {
			for (CommandDTO command : dto.getCommands()) {
				this.submit(command.getCommandID(), command.getInterval(),
						command.getTimeUnit());
			}
		}
	}

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
		return new SessionDTO(this.getID(), this.getStatus(),
				new LinkedList<CommandDTO>(this.commands));
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
