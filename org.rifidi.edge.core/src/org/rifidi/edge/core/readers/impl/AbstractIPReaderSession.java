/**
 * 
 */
package org.rifidi.edge.core.readers.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readers.ByteMessage;
import org.rifidi.edge.core.readers.Command;
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
public abstract class AbstractIPReaderSession extends AbstractReaderSession {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractIPReaderSession.class);

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
	/** True if the socket is currently being connected. */
	private AtomicBoolean connecting = new AtomicBoolean(false);


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
		super(destination, template);
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
		if ((getStatus() == ReaderStatus.PROCESSING
				|| getStatus() == ReaderStatus.CREATED || getStatus() == ReaderStatus.LOGGINGIN)
				&& connecting.compareAndSet(false, true)) {
			try {
				setStatus(ReaderStatus.CONNECTING);
				socket=null;
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
					// revert
					setStatus(ReaderStatus.CREATED);
					connecting.compareAndSet(true, false);
					throw new IOException("Unable to reach reader.");
				}
				readThread = new Thread(new ReadThread(this, socket
						.getInputStream(), readQueue));
				writeThread = new Thread(new WriteThread(socket
						.getOutputStream(), writeQueue));
				readThread.start();
				writeThread.start();
				setStatus(ReaderStatus.LOGGINGIN);
				// do the logical connect
				try {
					if (!onConnect()) {
						setStatus(ReaderStatus.CREATED);
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
							setStatus(ReaderStatus.CREATED);
							connect();
						} catch (InterruptedException e) {
							Thread.currentThread().interrupt();
						} catch (IOException e) {
							logger.warn("Unable to reconnect.");
						}
					}

				};
				connectionGuardian.start();
				setStatus(ReaderStatus.PROCESSING);
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
			}finally {
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
				try {
					socket.close();
				} catch (IOException e) {
					logger.debug("Failed closing socket: " + e);
				}
				readThread.interrupt();
				writeThread.interrupt();
				setStatus(ReaderStatus.CREATED);
			}
		}
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


}
