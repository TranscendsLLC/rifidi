/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import java.io.IOException;
import java.net.Socket;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory;
import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategy;
import org.rifidi.edge.core.sensors.base.threads.MessageProcessingStrategyFactory;
import org.rifidi.edge.core.sensors.base.threads.ReadThread;
import org.rifidi.edge.core.sensors.base.threads.WriteThread;
import org.rifidi.edge.core.sensors.messages.ByteMessage;
import org.springframework.jms.core.JmsTemplate;

/**
 * Base implementation of a ReaderPlugin that uses TCP/IP as a means of
 * communicating with a physical reader. This base class handles socket I/O and
 * threads.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class AbstractIPSensorSession extends AbstractSensorSession {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractIPSensorSession.class);

	/** Host to connect to. */
	private String host;
	/** Port to connect to. */
	private int port;
	/** The socket used for connecting to the reader. */
	private Socket socket;
	/** Number reconnection attempts. */
	private int maxConAttempts;
	/** Interval between two connection attempts. */
	private int reconnectionInterval;
	/** Thread for reading from the socket. */
	private Thread readThread;
	/** Thread for writing to the socket. */
	private Thread writeThread;
	/** Queue for reading messages. */
	private LinkedBlockingQueue<ByteMessage> readQueue = new LinkedBlockingQueue<ByteMessage>();
	/** Queue for writing messages. */
	private LinkedBlockingQueue<ByteMessage> writeQueue = new LinkedBlockingQueue<ByteMessage>();

	/**
	 * This thread takes care that connections get recreated when the socket
	 * goes down.
	 */
	private Thread connectionGuardian;
	/** True if we are currently in the connect method. */
	private AtomicBoolean connecting = new AtomicBoolean(false);
	/** True if we are currently in the reconnect loop */
	private AtomicBoolean reconnectLoop = new AtomicBoolean(false);

	/**
	 * Constructor.
	 * 
	 * @param sensor
	 * @param host
	 *            HostName of the hardware reader
	 * @param port
	 *            Port of the hardware reader
	 * @param reconnectionInterval
	 *            Length of time between reconnects. Specified in MS
	 * @param maxConAttempts
	 *            Maximum number of reconnect attempts to make
	 * @param destination
	 *            JMS Queue to put tag data on
	 * @param template
	 *            JMSTemplate to use to send tag data
	 * @param commandFactory
	 */
	public AbstractIPSensorSession(AbstractSensor<?> sensor, String ID,
			String host, int port, int reconnectionInterval,
			int maxConAttempts, Destination destination, JmsTemplate template) {
		super(sensor, ID, destination, template);
		this.host = host;
		this.port = port;
		this.maxConAttempts = maxConAttempts;
		this.reconnectionInterval = reconnectionInterval;

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
		if ((getStatus() == SessionStatus.PROCESSING
				|| getStatus() == SessionStatus.CREATED
				|| getStatus() == SessionStatus.LOGGINGIN || getStatus() == SessionStatus.CLOSED)
				&& connecting.compareAndSet(false, true)) {
			try {
				setStatus(SessionStatus.CONNECTING);
				socket = null;
				// if an executor exists, execute it (delete the executor :))
				if (processing.get()) {
					if (!processing.compareAndSet(true, false)) {
						logger
								.warn("Killed a non active executor. That should not happen. ");
					}
					executor.shutdownNow();
					executor = null;
					for (Integer id : commands.keySet()) {
						idToData.get(id).future = null;
					}
				}
				// check if somebody is currently reading
				if (readThread != null) {
					readThread.interrupt();
					try {
						logger.debug("Killing read thread.");
						readThread.join();
						readThread = null;
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
						return;
					}
				}
				// check if somebody is currently writing
				if (writeThread != null) {
					writeThread.interrupt();
					try {
						logger.debug("Killing write thread.");
						writeThread.join();
						writeThread = null;
					} catch (InterruptedException e1) {
						Thread.currentThread().interrupt();
						return;
					}
				}
				executor = new ScheduledThreadPoolExecutor(1);
				// try to open the socket
				logger.info("Attempting to connect to : " + host + ":" + port);

				// begin reconnect loop. We have an atomic boolean that should
				// be true as long as we are in the loop
				if (!reconnectLoop.compareAndSet(false, true)) {
					logger.warn("atomic boolean for recconnect "
							+ "loop was already true.");
				}
				try {
					// reconnect loop. try to connect maxConAteempts number of
					// times, unless maxConAttempts is -1, in which case try
					// forever.
					for (int connCount = 0; connCount < maxConAttempts
							|| maxConAttempts == -1; connCount++) {
						try {
							socket = new Socket(host, port);
							break;
						} catch (IOException e) {
							logger
									.debug("Unable to connect to reader on try nr "
											+ connCount + " " + e);
						}

						// wait for a specified number of ms or for a notify
						// call
						try {
							synchronized (reconnectLoop) {
								reconnectLoop.wait(this.reconnectionInterval);
							}
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
							break;
						}
						if (!reconnectLoop.get())
							break;
					}
				} finally {
					reconnectLoop.compareAndSet(true, false);
				}

				// no socket, we are screwed
				if (socket == null) {
					// revert
					setStatus(SessionStatus.CREATED);
					connecting.compareAndSet(true, false);
					throw new IOException("Unable to reach reader.");
				}
				readThread = new Thread(
						new ReadThread(socket.getInputStream(),
								getMessageParsingStrategyFactory(),
								new QueueingMessageProcessingStrategyFactory(
										readQueue)));
				writeThread = new Thread(new WriteThread(socket
						.getOutputStream(), writeQueue));
				readThread.start();
				writeThread.start();
				setStatus(SessionStatus.LOGGINGIN);
				// do the logical connect
				try {
					if (!onConnect()) {
						setStatus(SessionStatus.CREATED);
						logger.warn("Unable to connect to reader " + host + ":"
								+ port);
						return;
					}
				} catch (IOException e) {
					logger.debug("Unable to connect during login: " + e);
					connecting.compareAndSet(true, false);
					connect();
				}
				logger.debug("Connected.");
				// create thread that checks if the write thread dies and
				// restart it
				connectionGuardian = new Thread() {

					/*
					 * (non-Javadoc)
					 * 
					 * @see java.lang.Thread#run()
					 */
					@Override
					public void run() {
						try {
							readThread.join();
							// don't try to connect again if we are closing down
							if (getStatus() != SessionStatus.CLOSED) {
								setStatus(SessionStatus.CREATED);
								readQueue.clear();
								writeQueue.clear();
								connect();
							}
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						} catch (IOException e) {
							logger.warn("Unable to reconnect.");
						}
					}

				};
				connectionGuardian.start();
				setStatus(SessionStatus.PROCESSING);
				if (!processing.compareAndSet(false, true)) {
					logger.warn("Executor was already active! ");
				}
				// resubmit commands
				while (commandQueue.peek() != null) {
					executor.submit(commandQueue.poll());
				}
				synchronized (commands) {
					for (Integer id : commands.keySet()) {
						if (idToData.get(id).future == null) {
							idToData.get(id).future = executor
									.scheduleWithFixedDelay(commands.get(id),
											0, idToData.get(id).interval,
											idToData.get(id).unit);
						}
					}
				}
			} finally {
				connecting.compareAndSet(true, false);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.newreaders.Reader#disconnect()
	 */
	@Override
	public void disconnect() {
		if (processing.get()) {
			if (processing.compareAndSet(true, false)) {
				setStatus(SessionStatus.CLOSED);
				synchronized (commands) {
					for (Integer id : commands.keySet()) {
						if (idToData.get(id).future != null) {
							idToData.get(id).future.cancel(true);
							idToData.get(id).future = null;
						}
					}
				}
				this.executor.shutdown();
				try {
					socket.close();
				} catch (IOException e) {
					logger.debug("Failed closing socket: " + e);
				}
				readThread.interrupt();
				writeThread.interrupt();

			}
		}
		// this is intended to be able to stop the connect method when we are in
		// the reconnect loop
		if (reconnectLoop.getAndSet(false)) {
			synchronized (reconnectLoop) {
				reconnectLoop.notify();
			}
			this.executor.shutdown();
			this.executor = null;

		}
	}

	private class QueueingMessageProcessingStrategyFactory implements
			MessageProcessingStrategyFactory {

		private Queue<ByteMessage> queue;

		public QueueingMessageProcessingStrategyFactory(Queue<ByteMessage> queue) {
			this.queue = queue;
		}

		@Override
		public MessageProcessingStrategy createMessageProcessor() {
			return new QueueingMessageProcessingStrategy(queue);
		}

	}

	/**
	 * Called after the initial socket connection got established.
	 * 
	 * @return true if the connect was successful
	 * @throws IOException
	 *             if a connection problem occurs
	 */
	public abstract boolean onConnect() throws IOException;

	/**
	 * Get a MessageParsingStrategy for this IP based sensor session.
	 * 
	 * @return
	 */
	public abstract MessageParsingStrategyFactory getMessageParsingStrategyFactory();

	@Override
	public String toString() {
		return "IPSession: " + host+":"+port;
	}
	
	

}
