/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.ReaderSession;
import org.rifidi.edge.core.sensors.commands.Command;
import org.springframework.jms.core.JmsTemplate;

/**
 * An Abstract class for concreate ReaderSessions to extend. This
 * AbstractSensorSession class provides a base implementation of the command
 * handeling of a reader session (i.e. submitting, deleting, executing
 * commands). It does not provide any implementation of connection methods in a
 * session
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractSensorSession extends ReaderSession {

	/** Used to execute commands. */
	protected ScheduledThreadPoolExecutor executor;
	/** Status of the reader. */
	private SessionStatus status;
	/** Map containing the periodic commands with the process id as key. */
	protected Map<Integer, Command> commands;
	/** Map containing command process id as key and the future as value. */
	protected Map<Integer, CommandExecutionData> idToData;
	/** Job counter */
	private int counter = 0;
	/** JMS destination. */
	private Destination destination;
	/** Spring jms template */
	private JmsTemplate template;
	/** True if the executor is up and running. */
	protected AtomicBoolean processing = new AtomicBoolean(false);
	/**
	 * Queue for single-shot commands that get submitted while the executor is
	 * inactive.
	 */
	protected Queue<Command> commandQueue = new ConcurrentLinkedQueue<Command>();
	/** Logger */
	private static final Log logger = LogFactory
			.getLog(AbstractSensorSession.class);

	/**
	 * Constructor
	 * 
	 * @param ID
	 *            ID of this ReaderSession
	 * @param destination
	 *            The JMS Queue to add Tag Data to
	 * @param template
	 *            The Template used to send Tag data to the internal queue
	 */
	public AbstractSensorSession(String ID, Destination destination,
			JmsTemplate template) {
		super(ID);
		this.commands = new HashMap<Integer, Command>();
		this.idToData = new HashMap<Integer, CommandExecutionData>();
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
	public Map<Integer, Command> currentCommands() {
		return new HashMap<Integer, Command>(commands);
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
	 * @see
	 * org.rifidi.edge.core.readers.ReaderSession#submit(org.rifidi.edge.core
	 * .readers.Command)
	 */
	@Override
	public void submit(Command command) {
		command.setReaderSession(this);
		command.setTemplate(template);
		command.setDestination(destination);
		if (processing.get()) {
			executor.submit(command);
			return;
		}
		commandQueue.add(command);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.ReaderSession#submit(org.rifidi.edge.core
	 * .readers.Command, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(Command command, long interval, TimeUnit unit) {
		synchronized (commands) {
			command.setReaderSession(this);
			command.setTemplate(template);
			command.setDestination(destination);
			Integer id = counter++;
			commands.put(id, command);
			CommandExecutionData data = new CommandExecutionData();
			data.interval = interval;
			data.unit = unit;
			if (processing.get()) {
				data.future = executor.scheduleWithFixedDelay(command, 0,
						interval, unit);
			}
			idToData.put(id, data);
			return id;
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

}
