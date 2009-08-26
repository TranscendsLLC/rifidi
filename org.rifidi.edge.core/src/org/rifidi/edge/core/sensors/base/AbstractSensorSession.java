/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
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
import org.rifidi.edge.core.sensors.commands.Command;
import org.springframework.jms.core.JmsTemplate;

/**
 * An Abstract class for concreate ReaderSessions to extend. This
 * AbstractSensorSession class provides a base implementation of the command
 * handling of a reader session (i.e. submitting, deleting, executing commands).
 * It does not provide any implementation of connection methods in a session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractSensorSession extends SensorSession {

	/** Used to execute commands. */
	protected volatile ScheduledThreadPoolExecutor executor;
	/** Status of the reader. */
	private volatile SessionStatus status;
	/** Map containing the periodic commands with the process commandID as key. */
	protected final Map<Integer, CommandExecutor> commands;
	/** Map containing the periodic commands with the process commandID as key. */
	protected final Map<String, CommandExecutor> commandIDToExecutor;
	/** Map containing command process commandID as key and the future as value. */
	protected final Map<Integer, CommandExecutionData> idToData;
	/** Job counter */
	private AtomicInteger counter = new AtomicInteger(0);
	/** JMS destination. */
	private volatile Destination destination;
	/** Spring jms template */
	private volatile JmsTemplate template;
	/** True if the executor is up and running. */
	protected AtomicBoolean processing = new AtomicBoolean(false);
	/**
	 * Queue for single-shot commands that get submitted while the executor is
	 * inactive.
	 */
	protected Queue<CommandExecutor> commandQueue = new ConcurrentLinkedQueue<CommandExecutor>();
	/** Logger */
	private static final Log logger = LogFactory
			.getLog(AbstractSensorSession.class);

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
	 */
	public AbstractSensorSession(AbstractSensor<?> sensor, String ID,
			Destination destination, JmsTemplate template) {
		super(ID, sensor);
		this.commands = new ConcurrentHashMap<Integer, CommandExecutor>();
		this.commandIDToExecutor = new ConcurrentHashMap<String, CommandExecutor>();
		this.idToData = new ConcurrentHashMap<Integer, CommandExecutionData>();
		this.template = template;
		this.destination = destination;
		status = SessionStatus.CREATED;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readers.ReaderSession#currentCommands()
	 */
	@Override
	public Map<Integer, String> currentCommands() {
		Map<Integer, String> ret = new HashMap<Integer, String>();
		for (Integer id : commands.keySet()) {
			ret.put(id, commands.get(id).getCommandID());
		}
		return ret;
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
		commands.remove(id);
		idToCommandDTO.remove(id);
		CommandExecutionData data = idToData.remove(id);
		if (data != null) {
			if (data.future != null) {
				data.future.cancel(true);
			}
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
		CommandExecutor exec = new CommandExecutor(commandID, this);
		commands.put(id, exec);
		commandIDToExecutor.put(commandID, exec);
		CommandExecutionData data = new CommandExecutionData();
		data.interval = interval;
		data.unit = unit;
		idToCommandDTO.put(id, new CommandDTO(true, interval, unit, id,
				commandID));
		if (processing.get()) {
			data.future = executor.scheduleWithFixedDelay(exec, 0, interval,
					unit);
		}
		idToData.put(id, data);
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#submit(java.lang.String)
	 */
	@Override
	public void submit(String commandID) {
		CommandExecutor exec = new CommandExecutor(commandID, this);
		if (processing.get()) {
			executor.submit(exec);
			return;
		}
		commandQueue.add(exec);
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
		CommandExecutor exec = new CommandExecutor(command, this);
		if (processing.get()) {
			executor.submit(exec);
			return;
		}
		commandQueue.add(exec);
	}

	/**
	 * Called if a command configuration temporarily disappears.
	 * 
	 * @param commandID
	 */
	public void suspendCommand(String commandID) {
		if (logger.isDebugEnabled()) {
			logger.debug("Suspending " + commandID);
		}
		if (commandIDToExecutor.get(commandID) != null) {
			commandIDToExecutor.get(commandID).suspend();
		}
	}

	/**
	 * Private class used to wrap an executing command
	 * 
	 * @author Jochen Mader - jochen@pramari.com
	 * 
	 */
	protected class CommandExecutionData {
		public long interval;
		public TimeUnit unit;
		public ScheduledFuture<?> future;
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

	protected abstract Command getCommandInstance(String commandID);

	private class CommandExecutor implements Runnable {

		private String commandID;
		private volatile Command instance;
		private SensorSession sensorSession;

		/**
		 * @param commandID
		 * @param instance
		 * @param sensorSession
		 */
		public CommandExecutor(String commandID, SensorSession sensorSession) {
			this.commandID = commandID;
			this.sensorSession = sensorSession;
		}

		/**
		 * @param instance
		 * @param sensorSession
		 */
		public CommandExecutor(Command instance, SensorSession sensorSession) {
			super();
			this.commandID = instance.getCommandID();
			this.instance = instance;
			this.sensorSession = sensorSession;
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
				t.printStackTrace();
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
