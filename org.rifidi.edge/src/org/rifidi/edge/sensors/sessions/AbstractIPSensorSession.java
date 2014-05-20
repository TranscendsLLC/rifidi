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

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.ByteMessage;
import org.rifidi.edge.sensors.Command;

/**
 * Base implementation of a ReaderPlugin that uses TCP/IP as a means of
 * communicating with a physical reader. This base class handles socket I/O and
 * threads.
 * 
 * Some important features:
 * 
 * Handles automatic reconnection using two variables:
 * maxNumReconnectionAttempts and reconnectInterval. If maxNumReconnectAttempts
 * is set to -1, it will try to reconnect forever. reconnectInterval is the time
 * in between two successive reconnection attempts.
 * 
 * Provides an onConnect method that is the first thing that is executed when a
 * valid TCP/IP connection has been established. The implementation can, at this
 * point send login credentials or receive a welcome message.
 * 
 * The MessageParsingStrategy lets the implementation determine when the bytes
 * form a logical message.
 * 
 * The messageProcessingStrategy lets the implementaion determine what to do
 * with the receieved messages
 * 
 * @author Jochen Mader - jochen@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class AbstractIPSensorSession extends AbstractSensorSession {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(AbstractIPSensorSession.class);

	/** Host to connect to. */
	private final String host;
	/** Port to connect to. */
	private final int port;
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
	 * @param commandConfigurations
	 * 
	 */
	public AbstractIPSensorSession(AbstractSensor<?> sensor, String ID,
			String host, int port, int reconnectionInterval,
			int maxConAttempts,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID, commandConfigurations);
		this.host = host;
		this.port = port;
		this.maxConAttempts = maxConAttempts;
		this.reconnectionInterval = reconnectionInterval;

	}

	/**
	 * Get the host this session is connected to
	 * 
	 * @return the host
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Get the port this session is connected to
	 * 
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Send a message over the line. This method is protected so that subclasses
	 * can choose whether or not to expose it to clients.
	 * 
	 * @param o
	 * @throws IOException
	 */
	protected void sendMessage(ByteMessage message) throws IOException {
		writeQueue.add(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.newreaders.Reader#connect()
	 */
	@Override
	protected void _connect() throws IOException {
		if ((getStatus() == SessionStatus.PROCESSING
				|| getStatus() == SessionStatus.CREATED
				|| getStatus() == SessionStatus.LOGGINGIN || getStatus() == SessionStatus.CLOSED)
				&& connecting.compareAndSet(false, true)) {
			try {
				setStatus(SessionStatus.CONNECTING);
                //fix for abandoned sockets
                if (socket != null) {
                        try{ socket.close();} catch (Exception e) {}
                }
				socket = null;
				// if an executor exists, execute it (delete the executor :))
				if (processing.get()) {
					if (!processing.compareAndSet(true, false)) {
						logger
								.warn("Killed a non active executor. That should not happen. ");
					}
					// TODO: better would be to have a method in
					// AbstractSensorSession that handles the shutdown of the
					// executor
					executor.shutdownNow();
					executor = null;
					resetCommands();
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
							if (logger.isDebugEnabled()) {
								logger.info("Socket connection successful to "
										+ this.host + ":" + this.port);
							}
							break;
						} catch (IOException e) {
							// do nothing
						}

						// print info message the first time
						if (logger.isInfoEnabled() && connCount == 0) {
							logger.info("Session " + this.getID() + " on "
									+ this.getSensor().getID()
									+ " Starting reconnect attempts to "
									+ this.host + ":" + this.port);
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

				readThread = new Thread(new ReadThread(socket.getInputStream(),
						getMessageParsingStrategyFactory(),
						getMessageProcessingStrategyFactory()));
				writeThread = new Thread(new WriteThread(socket
						.getOutputStream(), writeQueue));
				readThread.start();
				writeThread.start();
				setStatus(SessionStatus.LOGGINGIN);
				// do the logical connect
				try {
					if (!onConnect()) {
						onConnectFailed();
						return;
					}
				} catch (IOException e) {
					logger.warn("Unable to connect during login: " + e);
					connecting.compareAndSet(true, false);
					connect();
				}
				logger.info("Session " + this.getID() + " on "
						+ this.getSensor().getID()
						+ " connected successfully to  " + this.host + ":"
						+ this.port);

				// TODO: paramaterize the keepalive frequency
				submit(getKeepAliveCommand(), 10L, TimeUnit.SECONDS);

				// create thread that checks if the write thread dies and
				// restart it
				connectionGuardian = new ConnectionGaurdian();
				connectionGuardian.start();
				setStatus(SessionStatus.PROCESSING);
				if (!processing.compareAndSet(false, true)) {
					logger.warn("Executor was already active! ");
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
				super.resetCommands();
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

	/**
	 * Called after the initial socket connection got established.
	 * Implementations can use this method to send login credentials or receive
	 * a welcome message.
	 * 
	 * @return true if the connect was successful
	 * @throws IOException
	 *             if a connection problem occurs
	 */
	protected abstract boolean onConnect() throws IOException;

	/**
	 * This method is called if onConnect fails.
	 */
	protected void onConnectFailed() {
		setStatus(SessionStatus.CLOSED);
		logger.warn("Connection Failed " + host + ":" + port);
		disconnect();
		connecting.set(false);
		this.executor.shutdown();
		try {
			socket.close();
		} catch (IOException e) {
			logger.debug("Failed closing socket: " + e);
		}
		readThread.interrupt();
		writeThread.interrupt();

		return;
	}

	/**
	 * Get a factory for MessageParsingStrategy objects
	 * 
	 * @return
	 */
	protected abstract MessageParsingStrategyFactory getMessageParsingStrategyFactory();

	/**
	 * Get a Factory for MessageProcessingStrategy objects.
	 * 
	 * @return
	 */
	protected abstract MessageProcessingStrategyFactory getMessageProcessingStrategyFactory();

	/**
	 * This method is called when resetting the connection. It should wipe out
	 * all undelivered messages so that the client does not receive old
	 * messages.
	 */
	protected abstract void clearUndelieverdMessages();

	/**
	 * This method should return a command that can be used as a test to either
	 * make sure the TCP/IP connection does not close or to see whether or not
	 * the connection is still active. By default, the command does nothing.
	 * Typically the implementation of this should just use some command from
	 * the reader's API that involves a short status message.
	 * 
	 * @return
	 */
	protected Command getKeepAliveCommand() {
		return new Command("Default Keep Alive") {
			@Override
			public void run() {
				// Do nothing
			}
		};
	}

	@Override
	public String toString() {
		return "IPSession: " + host + ":" + port + " (" + getStatus() + ")";
	}

	/**
	 * A thread that waits for the readthread to die, then attempts to
	 * reconnect, as long as the status isn't CLOSED
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	private class ConnectionGaurdian extends Thread {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {

			try {

				readThread.join();

				try {
					socket.close();
				} catch (Exception e) {
					e.printStackTrace();
				}

				// don't try to connect again if we are closing down
				if (getStatus() != SessionStatus.CLOSED) {
					setStatus(SessionStatus.CREATED);
					writeQueue.clear();
					clearUndelieverdMessages();
					connect();
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (IOException e) {
				logger.warn("Unable to reconnect.");
				disconnect();
			}
		}

	}

}
