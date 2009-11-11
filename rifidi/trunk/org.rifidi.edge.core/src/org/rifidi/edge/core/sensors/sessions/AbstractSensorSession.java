/*
 * 
 * AbstractSensorSession.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.core.sensors.sessions;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.rmi.dto.CommandDTO;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.sensors.commands.Command;
import org.springframework.jms.core.JmsTemplate;

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
	/** JMS destination. */
	private volatile Destination destination;
	/** Spring jms template */
	private volatile JmsTemplate template;
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
			Destination destination, JmsTemplate template,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(ID, sensor);
		this.template = template;
		this.destination = destination;
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
		this.queuedCommands.clear();
		List<CommandExecutor> commandExs = new LinkedList<CommandExecutor>(
				this.runningCommands);
		// transfer all running commands to queued commands, preserving order
		// and canceling the future if the command was scheduled.
		for (CommandExecutor commandEx : commandExs) {
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
	 * @see org.rifidi.edge.core.sensors.SensorSession#submitQueuedCommands()
	 */
	@Override
	protected void submitQueuedCommands() {
		while (!queuedCommands.isEmpty()) {
			submitExecutor(queuedCommands.poll());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#submit(java.lang.String,
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
	 * org.rifidi.edge.core.sensors.SensorSession#submit(org.rifidi.edge.core
	 * .sensors.commands.Command)
	 */
	@Override
	public void submit(Command command) {
		command.setReaderSession(this);
		CommandExecutor commandExec = new CommandExecutor(command, this);
		if (processing.get()) {
			submitExecutor(commandExec);
		} else {
			logger.info("Session not in processing state. "
					+ "Command will be executed when session connects: "
					+ command.getCommandID());
			this.queuedCommands.add(commandExec);
		}
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
			executor.execute(ex);
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
		this.status = status;
	}

	/**
	 * @return the destination
	 */
	protected Destination getDestination() {
		return destination;
	}

	/**
	 * @return the template
	 */
	protected JmsTemplate getTemplate() {
		return template;
	}

	/**
	 * Used to aquire a command instance.
	 * 
	 * @param commandID
	 * @return
	 */
	protected Command getCommandInstance(String commandID) {
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
		private Long interval = 0L;
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
		 * Use this constructor for Internal Commands (single shot commands that
		 * are not saved using a DTO and should not be resubmitted if the
		 * session stops and is started again)
		 * 
		 * @param instance
		 * @param sensorSession
		 */
		public CommandExecutor(Command instance, SensorSession sensorSession) {
			this.commandID = instance.getCommandID();
			this.instance = instance;
			this.sensorSession = sensorSession;
			this.isInternal = true;
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
						instance.setTemplate(template);
						instance.setDestination(destination);
					}
				}
				if (instance != null) {
					instance.run();
				}
			} catch (Throwable t) {
				logger.error("error: ", t);
			}
		}

		/**
		 * @param instance
		 *            the instance to set
		 */
		public void suspend() {
			this.instance = null;
		}

	}
}
