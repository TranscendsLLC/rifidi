/**
 * 
 */
package org.rifidi.edge.core.readers.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.ByteMessage;
import org.rifidi.edge.core.readers.Command;
import org.rifidi.edge.core.readers.ReaderSession;
import org.rifidi.edge.core.readers.ReaderStatus;
import org.rifidi.edge.core.readers.impl.threads.ReadThread;
import org.rifidi.edge.core.readers.impl.threads.WriteThread;
import org.springframework.jms.core.JmsTemplate;

/**
 * Base implementation of a reader. Extend for your own readers.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractIPReaderSession implements ReaderSession {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractIPReaderSession.class);
	/** Queue for reading messages. */
	protected LinkedBlockingQueue<ByteMessage> readQueue = new LinkedBlockingQueue<ByteMessage>();
	/** Queue for writing messages. */
	protected LinkedBlockingQueue<ByteMessage> writeQueue = new LinkedBlockingQueue<ByteMessage>();
	/** Used to execute commands. */
	protected ScheduledThreadPoolExecutor executor;
	/** Host to connect to. */
	protected String host;
	/** Port to connect to. */
	protected int port;
	/** The socket used for connecting to the reader. */
	protected Socket socket;
	/** Number reconnection attempts. */
	protected int maxConAttempts;
	/** Interval between two connection attempts. */
	protected int reconnectionInterval;
	/** Thread for reading from the socket. */
	protected Thread readThread;
	/** Thread for writing to the socket. */
	protected Thread writeThread;
	/** Status of the reader. */
	protected ReaderStatus status;
	/** Map containing the periodic commands with the process id as key. */
	protected Map<Integer, Command> commands;
	/** Map containing command process id as key and the future as value. */
	protected Map<Integer, ScheduledFuture<?>> idToFuture;
	/** Job counter */
	protected int counter = 0;
	/** JMS destination. */
	protected Destination destination;
	/** Spring jms template */
	protected JmsTemplate template;

	/**
	 * Constructor.
	 * 
	 * @param host
	 * @param port
	 * @param nrThreads
	 * @param reconnectionInterval
	 * @param maxConAttempts
	 */
	public AbstractIPReaderSession(String host, int port,
			int reconnectionInterval, int maxConAttempts,
			Destination destination, JmsTemplate template) {
		executor = new ScheduledThreadPoolExecutor(1);
		this.host = host;
		this.port = port;
		this.maxConAttempts = maxConAttempts;
		this.reconnectionInterval = reconnectionInterval;
		this.commands = new HashMap<Integer, Command>();
		this.idToFuture = new HashMap<Integer, ScheduledFuture<?>>();
		this.template = template;
		this.destination = destination;
		status = ReaderStatus.CREATED;
	}

	/**
	 * Check if a new message is available.
	 * 
	 * @return
	 * @throws IOException
	 */
	public boolean isMessageAvailable() throws IOException {
		return readQueue.peek() != null;
	}

	/**
	 * Receive a message. This method blocks if there isn't a message available.
	 * 
	 * @return
	 * @throws IOException
	 */
	public ByteMessage receiveMessage() throws IOException {
		try {
			ByteMessage ret = readQueue.take();
			return ret;
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException();
		}
	}

	/**
	 * Receive a message. This method blocks for the given amount of time. If
	 * the time expires the method will return.
	 * 
	 * @param timeout
	 * @return
	 * @throws IOException
	 */
	public ByteMessage receiveMessage(long timeout) throws IOException {
		try {
			return readQueue.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException();
		}
	}

	/**
	 * Send a message over the line.
	 * 
	 * @param o
	 * @throws IOException
	 */
	public void sendMessage(ByteMessage message) throws IOException {
		writeQueue.add(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.newreaders.Reader#connect()
	 */
	@Override
	public void connect() throws IOException {
		status = ReaderStatus.CONNECTING;
		// try to open the socket
		for (int connCount = 0; connCount < maxConAttempts; connCount++) {
			try {
				socket = new Socket(host, port);
				break;
			} catch (IOException e) {
				logger.warn("Unable to connect to reader on try nr "
						+ connCount + " " + e);
			}
			// sleep between to connection attempts
			try {
				Thread.sleep(reconnectionInterval);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return;
			}
		}
		// no socket, we are screwed
		if (socket == null) {
			throw new IOException("Unable to reach reader.");
		}
		readThread = new Thread(new ReadThread(this, socket.getInputStream(),
				readQueue));
		writeThread = new Thread(new WriteThread(socket.getOutputStream(),
				writeQueue));
		readThread.start();
		writeThread.start();
		status = ReaderStatus.LOGGINGIN;
		if (!onConnect()) {
			status = ReaderStatus.FAIL;
			logger.warn("Unable to connect to reader " + host + ":" + port);
			return;
		}
		logger.debug("Connected.");
		status = ReaderStatus.PROCESSING;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.newreaders.Reader#disconnect()
	 */
	@Override
	public Set<Command> disconnect() {
		try {
			socket.close();
		} catch (IOException e) {
			logger.debug("Failed closing socket: " + e);
		}
		readThread.interrupt();
		writeThread.interrupt();
		return Collections.emptySet();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.newreaders.Reader#submit(org.rifidi.edge.core.newreaders
	 * .Command)
	 */
	@Override
	public void submit(Command command) {
		command.setInput(readQueue);
		command.setOutput(writeQueue);
		command.setReaderSession(this);
		command.setTemplate(template);
		command.setDestination(destination);
		executor.submit(command);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.ReaderSession#submit(org.rifidi.edge.core
	 * .readers.Command, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public synchronized Integer submit(Command command, long interval,
			TimeUnit unit) {
		command.setInput(readQueue);
		command.setOutput(writeQueue);
		command.setReaderSession(this);
		command.setTemplate(template);
		command.setDestination(destination);
		Integer id = counter++;
		commands.put(id, command);
		idToFuture.put(id, executor.scheduleWithFixedDelay(command, 0,
				interval, unit));
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readers.ReaderSession#killComand(java.lang.Integer)
	 */
	@Override
	public synchronized void killComand(Integer id) {
		commands.remove(id);
		ScheduledFuture<?> future = idToFuture.remove(id);
		if (future != null) {
			future.cancel(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.newreaders.Reader#currentCommands()
	 */
	@Override
	public Map<Integer, Command> currentCommands() {
		return new HashMap<Integer, Command>(commands);
	}

	/**
	 * This method is called each time a new byte is read. It will return the
	 * full message if a complete message has arrived, other wise null.
	 * 
	 * @param message
	 * @return the message or null
	 */
	public abstract byte[] isMessage(byte message);

	/**
	 * Called after the initial socket connection got established.
	 * 
	 * @return true if the connect was successful
	 * @throws IOException
	 *             if a connection problem occurs
	 */
	public abstract boolean onConnect() throws IOException;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.newreaders.Reader#getStatus()
	 */
	@Override
	public ReaderStatus getStatus() {
		return status;
	}

}
