/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
/**
 * 
 */
package org.rifidi.edge.sensors.sessions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.CommandDTO;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.notification.SensorProcessingEvent;
import org.rifidi.edge.notification.SensorClosedEvent;
import org.rifidi.edge.notification.SensorConnectingEvent;
import org.rifidi.edge.notification.SensorLoggingInEvent;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.Command;
import org.rifidi.edge.sensors.SensorSession;

/**
 * An Abstract class for concrete ReaderSessions to extend. This
 * AbstractSensorSession class provides a base implementation of the command
 * handling of a reader session (i.e. submitting, deleting, executing commands).
 * It does not provide any implementation of connection methods in a session.
 * 
 * This implementation has two lists to keep up with submitted commands: the
 * queuedCommands are commands that have been submitted to the session when the
 * session was not in the processing (i.e. connected) state. These commands will
 * be executed as soon as a connection has been established. The running
 * commands are the commands that have already been submitted to the executor.
 * 
 * When the session goes down for whatever reason, sublcasses can call the
 * resetCommands() method, which takes all the commands in the runningCommands
 * and moves them to the queuedCommands. When the session comes back up, they
 * may call the submitQueuedCommands() method, which takes all the commands
 * saved in the queuedCommands list and submits them back to the executor. This
 * way commands can be restored if there are interrupts in the session.
 * 
 * It is important for sublclasses to use the processing and executor objects
 * correctly. Whenever a connection has been established with the session,
 * subclasses should create a new ScheduledThreadPoolExecutor, assign it to the
 * executor, and set the processing variable to true. When the connection is
 * stopped or becomes disconnected, the processing variable should be set to
 * false.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractSensorSession extends SensorSession {

	/** Used to execute commands. Should only have one thread */
	protected volatile ScheduledThreadPoolExecutor executor;
	/** Status of the reader. */
	private volatile SessionStatus status;
	/** Job counter */
	private AtomicInteger counter = new AtomicInteger(0);
	/** True if the executor is up and running. */
	protected AtomicBoolean processing = new AtomicBoolean(false);
	/** Supplied by spring. */
	protected final Set<AbstractCommandConfiguration<?>> commandConfigurations;
	/** Logger */
	private static final Log logger = LogFactory
			.getLog(AbstractSensorSession.class);
	/** Commands that have not yet been submitted to the executor */
	private final Queue<CommandExecutor> queuedCommands;
	/** Commands that have already been submitted to the executor */
	private final List<CommandExecutor> runningCommands;

	/**
	 * Constructor
	 * 
	 * @param sensor
	 * @param ID
	 *            ID of this SensorSession
	 * @param destination
	 *            The JMS Queue to add Tag Data to
	 * @param template
	 *            The Template used to send Tag data to the internal queue
	 * @param commandConfigurations
	 *            Provided by spring
	 */
	public AbstractSensorSession(AbstractSensor<?> sensor, String ID,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(ID, sensor);
		this.commandConfigurations = commandConfigurations;
		status = SessionStatus.CREATED;
		this.queuedCommands = new ConcurrentLinkedQueue<CommandExecutor>();
		this.runningCommands = Collections
				.synchronizedList(new LinkedList<CommandExecutor>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.ReaderSession#getStatus()
	 */
	@Override
	public SessionStatus getStatus() {
		return status;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.ReaderSession#killComand(java.lang.Integer)
	 */
	@Override
	public void killComand(Integer id) {
		// First remove the CommandDTO from the list of DTOs
		CommandDTO dtoToRemove = null;
		synchronized (commands) {
			for (CommandDTO dto : commands) {
				if (dto.getProcessID().equals(id)) {
					dtoToRemove = dto;
				}
			}
		}
		if (dtoToRemove != null) {
			commands.remove(dtoToRemove);
		}

		// Next we remove all commands that have been submitted to the executor
		// service, and remove them from the list. If the command has a
		// future(meaning if it was scheduled for recurring execution), cancel
		// the future.
		CommandExecutor commandEx = null;
		synchronized (runningCommands) {
			for (CommandExecutor ex : runningCommands) {
				if (ex.jobID.equals(id)) {
					commandEx = ex;
					break;
				}
			}
		}
		if (commandEx != null) {
			runningCommands.remove(commandEx);
			if (commandEx.future != null) {
				commandEx.future.cancel(true);
			}
		}

		// Finally remove the command from the queuedCommands. It will only
		// exist here if the command was submitted before the session was
		// started and the session was not started before the kill method was
		// called
		CommandExecutor executor = null;
		for (CommandExecutor ex : queuedCommands) {
			if (ex.jobID.equals(id)) {
				executor = ex;
				break;
			}
		}
		if (executor != null) {
			queuedCommands.remove(executor);
		}

	}

	/**
	 * This method takes all commands that have been submitted to the executor
	 * service, cancels their execution if they are recurring, and places them
	 * back on the queuedCommands to be started again once the session restarts
	 */
	protected void resetCommands() {
		logger.debug("Reset Commands");
		this.queuedCommands.clear();
		List<CommandExecutor> commandExs = new LinkedList<CommandExecutor>(
				this.runningCommands);
		// transfer all running commands to queued commands, preserving order
		// and canceling the future if the command was scheduled.
		for (CommandExecutor commandEx : commandExs) {
			logger.info("Queing command: " + commandEx);
			runningCommands.remove(commandEx);
			if (commandEx.future != null) {
				commandEx.future.cancel(true);
			}
			queuedCommands.add(commandEx);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#submitQueuedCommands()
	 */
	@Override
	protected void submitQueuedCommands() {
		if (processing.get()) {
			submit(getResetCommand(), false, -1, null);
			while (!queuedCommands.isEmpty()) {
				CommandExecutor e = queuedCommands.poll();
				logger.debug("Submitting Queued Command" + e);
				submitExecutor(e);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#submit(java.lang.String,
	 * long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(String commandID, long interval, TimeUnit unit) {
		Integer id = counter.getAndIncrement();
		CommandExecutor commandExec = null;
		if (interval > 0L) {
			commands.add(new CommandDTO(interval, unit, id, commandID));
			commandExec = new CommandExecutor(commandID, this, id, interval,
					unit);
		} else {
			commands.add(new CommandDTO((long) 0, null, id, commandID));
			commandExec = new CommandExecutor(commandID, this, id);
		}
		if (processing.get()) {
			submitExecutor(commandExec);
		} else {
			logger.info("Session not in processing state. "
					+ "Command will be executed when session connects: "
					+ commandID);
			this.queuedCommands.add(commandExec);
		}
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.SensorSession#submit(org.rifidi.edge.core
	 * .sensors.commands.Command)
	 */
	@Override
	public void submit(Command command) {
		submit(command, true, -1, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.SensorSession#submitAndBlock(org.rifidi.
	 * edge.core.sensors.commands.Command, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public boolean submitAndBlock(Command command, long timeout, TimeUnit unit) {
		// submit the command for execution
		CommandExecutor ex = submit(command, true, -1, null);
		try {
			// if the command was submitted, then we have a non-null future
			if (ex.future != null) {
				// if we have a timeout
				if (timeout > 0)
					ex.future.get(timeout, unit);
				// otherwise wait forever
				else
					ex.future.get();

				// if everything went ok, return true;
				return true;
			} else {
				logger.warn("Cannot wait on command " + command.getCommandID()
						+ " because the session is not "
						+ "in the processing state");
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			logger.warn("Command " + command.getCommandID()
					+ " was interrupted before it finished executing", e);
		} catch (TimeoutException e) {
			logger.warn("Timed out waiting on command "
					+ command.getCommandID(), e);
		}
		// For some reason, the command was not submitted, we timed out, or it
		// was canceled before it executed. return false.
		return false;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.SensorSession#submit(org.rifidi.edge.core
	 * .sensors.commands.Command, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public void submit(Command command, long interval, TimeUnit unit) {
		submit(command, true, interval, unit);

	}

	private CommandExecutor submit(Command command,
			boolean queueIfNotProcessing, long interval, TimeUnit unit) {
		command.setReaderSession(this);
		CommandExecutor commandExec = new CommandExecutor(command, this,
				interval, unit);
		if (processing.get()) {
			submitExecutor(commandExec);
		} else if (queueIfNotProcessing) {
			logger.info("Session not in processing state. "
					+ "Command will be executed when session connects: "
					+ command.getCommandID());
			this.queuedCommands.add(commandExec);
		}
		return commandExec;
	}

	/**
	 * This is a helper method to submit commandexecutors to the Executor. It
	 * assumes that we are in the processing state already
	 * 
	 * @param ex
	 *            The ComamndExecutor to submit
	 * @param addToRunning
	 *            If true, will add to the list of running commands, which will
	 *            get restarted if the session stops and is started again
	 */
	private void submitExecutor(CommandExecutor ex) {
		if (ex.interval > 0) {
			logger.info("Scheduling recurring command: " + ex.getCommandID());
			ScheduledFuture<?> future = this.executor.scheduleAtFixedRate(ex,
					0L, ex.interval, ex.timeunit);
			ex.future = future;
		} else {
			if (ex.getCommandID() == null || ex.getCommandID().equals("")) {
				logger.info("Executing single shot command: "
						+ ex.instance.getClass().getSimpleName());
			} else {
				logger.info("Executing single shot command: "
						+ ex.getCommandID());
			}
			ex.future = executor.schedule(ex, 0, TimeUnit.MILLISECONDS);
		}
		if (!ex.isInternal) {
			runningCommands.add(ex);
		}
	}

	/**
	 * Called if a command configuration temporarily disappears.
	 * 
	 * @param commandID
	 */
	public void suspendCommand(String commandID) {
		synchronized (runningCommands) {
			for (CommandExecutor ex : runningCommands) {
				if (ex.commandID.equals(commandID)) {
					ex.suspend();
				}
			}
		}
		synchronized (queuedCommands) {
			for (CommandExecutor ex : queuedCommands) {
				if (ex.commandID.equals(commandID)) {
					ex.suspend();
				}
			}
		}
	}

	/**
	 * Change the status of this session
	 * 
	 * @param status
	 *            The status to set
	 */
	protected synchronized void setStatus(SessionStatus status) {
		logger.debug("Changing state: " + status);
		SessionStatus oldStatus = this.status;
		this.status = status;
		if (status == SessionStatus.PROCESSING && oldStatus != SessionStatus.PROCESSING) {
			SensorProcessingEvent processingEvent = new SensorProcessingEvent(this
					.getSensor().getID(), System.currentTimeMillis(), this.getID());
			this.getSensor().sendEvent(processingEvent);
		}
		if (oldStatus == SessionStatus.PROCESSING && status != SessionStatus.PROCESSING) {
			SensorClosedEvent closedEvent = new SensorClosedEvent(
					this.getSensor().getID(), System.currentTimeMillis(), this.getID());
			this.getSensor().sendEvent(closedEvent);
		}
		if (oldStatus != SessionStatus.CLOSED && status == SessionStatus.CLOSED) {
			SensorClosedEvent closedEvent = new SensorClosedEvent(
					this.getSensor().getID(), System.currentTimeMillis(), this.getID());
			this.getSensor().sendEvent(closedEvent);
		}
		if (oldStatus != SessionStatus.CONNECTING && status == SessionStatus.CONNECTING) {
			SensorConnectingEvent connectingEvent = new SensorConnectingEvent(
					this.getSensor().getID(), System.currentTimeMillis(), this.getID());
			this.getSensor().sendEvent(connectingEvent);
		}
		if (oldStatus != SessionStatus.LOGGINGIN && status == SessionStatus.LOGGINGIN) {
			SensorLoggingInEvent loggingInEvent = new SensorLoggingInEvent(
					this.getSensor().getID(), System.currentTimeMillis(), this.getID());
			this.getSensor().sendEvent(loggingInEvent);
		}
	}

	/**
	 * Used to aquire a command instance.
	 * 
	 * @param commandID
	 * @return
	 */
	public Command getCommandInstance(String commandID) {
		for (AbstractCommandConfiguration<?> config : commandConfigurations) {
			if (config.getID().equals(commandID)) {
				return config.getCommand(this.getSensor().getID());
			}
		}
		return null;
	}

	/**
	 * A wrapper that goes around commands that executes them. It also contains
	 * the data about recurring commands that are executing (such as the future)
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 * 
	 */
	private class CommandExecutor implements Runnable {

		private String commandID;
		private volatile Command instance;
		private SensorSession sensorSession;
		private Integer jobID = -1;
		private long interval = 0L;
		private TimeUnit timeunit = TimeUnit.MILLISECONDS;
		private Future<?> future;
		private boolean isInternal;

		/**
		 * Use this Constructor for single-shot commands
		 * 
		 * @param commandID
		 * @param instance
		 * @param sensorSession
		 */
		public CommandExecutor(String commandID, SensorSession sensorSession,
				Integer jobID) {
			this.commandID = commandID;
			this.sensorSession = sensorSession;
			this.jobID = jobID;
			this.isInternal = false;
		}

		/**
		 * Use this constructor for Commands that should be scheduled for
		 * repeated Execution
		 * 
		 * @param commandID
		 * @param instance
		 * @param sensorSession
		 * @param interval
		 * @param timeunit
		 */
		public CommandExecutor(String commandID, SensorSession sensorSession,
				Integer jobID, Long interval, TimeUnit timeunit) {
			this.commandID = commandID;
			this.sensorSession = sensorSession;
			this.jobID = jobID;
			this.interval = interval;
			this.timeunit = timeunit;
			this.isInternal = false;
		}

		/**
		 * Use this constructor for Internal Commands (commands that are not
		 * saved using a DTO and should not be resubmitted if the session stops
		 * and is started again)
		 * 
		 * @param instance
		 * @param sensorSession
		 */
		public CommandExecutor(Command instance, SensorSession sensorSession,
				Long interval, TimeUnit timeunit) {
			this.commandID = instance.getCommandID();
			this.instance = instance;
			this.sensorSession = sensorSession;
			this.isInternal = true;
			this.interval = interval;
			this.timeunit = timeunit;
		}

		/**
		 * @return the commandID
		 */
		public String getCommandID() {
			return commandID;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				if (instance == null) {
					instance = getCommandInstance(commandID);
					if (instance != null) {
						instance.setReaderSession(sensorSession);
					}
				}
				if (instance != null) {
					instance.run();
				}
			} catch (Throwable t) {
				logger.error("error: " + t.getMessage());
			}
		}

		/**
		 * @param instance
		 *            the instance to set
		 */
		public void suspend() {
			this.instance = null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Object#toString()
		 */
		@Override
		public String toString() {
			return "CommandExecutor: " + getCommandID() + " " + sensorSession;
		}

	}
}
