package org.rifidi.edge.core.communication.impl;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.CommunicationStateListener;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.communication.threads.ReadThread;
import org.rifidi.edge.core.communication.threads.WriteThread;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

/**
 * This is the internal Object managing a Connection by utilizing the
 * ConnectionManager and the Protocol provided by a reader plugin. It's
 * responsible for opening the connect and reestablishing it if there occured a
 * error.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class Communication implements ConnectionExceptionListener {
	private static final Log logger = LogFactory.getLog(Communication.class);

	private CommunicationProtocol protocol;
	private ConnectionManager connectionManager;

	private ConnectionImpl connection = null;

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	private ReadThread readThread;
	private WriteThread writeThread;

	private Set<CommunicationStateListener> listeners = new HashSet<CommunicationStateListener>();

	private boolean cleanUP = false;

	/**
	 * Constructor
	 * 
	 * @param connectionManager
	 *            ConnectionManager of the ReaderPlugin this Connection belongs
	 *            to
	 */
	public Communication(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
		protocol = this.connectionManager.getCommunicationProtocol();
		readQueue = new LinkedBlockingQueue<Object>();
		writeQueue = new LinkedBlockingQueue<Object>();
		connection = new ConnectionImpl(readQueue, writeQueue);
	}

	/**
	 * Open the connection to the reader
	 * 
	 * @return a Connection to read from and write to
	 */
	public Connection connect() {
		try {
			physicalConnect();

			// Create logical connection
			logger.debug("logical connection attempted");
			connectionManager.connect(connection);

			changeState(ConnectionStatus.CONNECTED);

		} catch (RifidiConnectionException e) {
			try {
				reconnect();
			} catch (RifidiConnectionException e1) {
				changeState(ConnectionStatus.ERROR);
			}
		} catch (RuntimeException e) {
			logger.error("Runtime Exception detected:"
					+ " Probably the connectionmanager is not safe", e);
			connection = null;
			changeState(ConnectionStatus.ERROR);
			return null;
		}
		return connection;
	}

	/**
	 * Disconnect the Connection to the reader
	 */
	public void disconnect() {
		try {
			changeState(ConnectionStatus.DISCONNECTED);

			if (writeThread != null)
				writeThread.ignoreExceptions(true);
			if (readThread != null)
				readThread.ignoreExceptions(true);

			if (connection != null){
				connectionManager.disconnect(connection);
				connection.cleanQueues();
			}
			if (writeThread != null) {
				writeThread.stop();
			}
			if (readThread != null) {
				readThread.stop();
			}

		} catch (RuntimeException e) {
			logger.error("Runtime Exception detected:"
					+ " Probably the connectionmanager is not safe", e);
			connection = null;
			// changeState(ConnectionStatus.ERROR);
			/*
			 * Can't call change state the normal way... It will cause an
			 * infinite loop.
			 */
			for (CommunicationStateListener listener : listeners) {
				listener.conn_error();
			}
			return;

		}
	}

	/**
	 * Reconnect the Connection. Used if the connection was already established.
	 * 
	 * @throws RifidiConnectionException
	 *             if MaxConnection attempts where reached
	 */
	private void reconnect() throws RifidiConnectionException {
		int maxConn;
		int x = 0;
		try {
			maxConn = connectionManager.getMaxNumConnectionsAttemps();
			while (x < maxConn || maxConn == 0) {
				if (maxConn != 0) {
					x++;
				}
				// sleep
				try {
					Thread.sleep(connectionManager.getReconnectionIntervall());
				} catch (InterruptedException e1) {
					// ignore this exception.
				}
				logger.debug("Trying to reconnect " + (maxConn - x)
						+ " more times");
				// try to reconnect
				try {
					physicalConnect();

					logger.debug("logical connection attempted");
					connectionManager.connect(connection);
				} catch (RifidiConnectionException e) {
					continue;
				}
				// hay!!! we succeeded!!
				break;
			}
		} catch (RuntimeException e) {
			logger.error("Runtime Exception detected:" + " Probably the "
					+ connectionManager.getClass().getName() + " is not safe",
					e);
			connection = null;
			changeState(ConnectionStatus.ERROR);
			return;
		}
		/* fire events */
		if (x == maxConn) {
			throw new RifidiConnectionException("MAX_CONNECTION_ATTEMPS");
		} else {
			changeState(ConnectionStatus.CONNECTED);
		}
	}

	/**
	 * Create a physical connection
	 * 
	 * @throws RifidiConnectionException
	 *             if the connection could not be established
	 */
	private void physicalConnect() throws RifidiConnectionException {
		logger.debug("physical connection attempted");
		ConnectionStreams connectionStreams = connectionManager
				.createCommunication();
		readThread = new ReadThread(connectionManager.toString() + " {"
				+ readQueue.hashCode() + "} Read Thread", this, protocol,
				readQueue, connectionStreams.getInputStream());

		writeThread = new WriteThread(connectionManager.toString() + " {"
				+ writeQueue.hashCode() + "} Write Thread", this, protocol,
				writeQueue, connectionStreams.getOutputStream());
		readThread.start();
		writeThread.start();
	}

	/**
	 * Add a listener for ConnectionEvents
	 * 
	 * @param listener
	 *            listener to notify about connection events
	 */
	public void addCommunicationStateListener(
			CommunicationStateListener listener) {
		listeners.add(listener);
	}

	/**
	 * Remove a listener
	 * 
	 * @param listener
	 *            to remove
	 */
	public void removeCommunicationStateListener(
			CommunicationStateListener listener) {
		listeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() {
		listeners.clear();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener#connectionExceptionEvent(java.lang.Exception)
	 */
	@Override
	public void connectionExceptionEvent(Exception exception) {
		logger.debug("Connection Exception Event");
		synchronized (this) {
			if (!cleanUP) {
				cleanUP = true;
				new Thread(new CleanUP(exception), "ReconnectingThread: ["
						+ connectionManager.toString() + "]").start();
			}
		}
	}

	/**
	 * This is the central method to change a connection state. It will
	 * automatically notify all Listeners about the connection event.
	 * 
	 * @param communicationState
	 *            new state of the connection
	 */
	private void changeState(ConnectionStatus communicationState) {
		if (communicationState == ConnectionStatus.CONNECTED) {
			for (CommunicationStateListener listener : listeners) {
				listener.conn_connected();
			}
		} else if (communicationState == ConnectionStatus.DISCONNECTED) {
			for (CommunicationStateListener listener : listeners) {
				listener.conn_disconnected();
			}

		} else if (communicationState == ConnectionStatus.ERROR) {
			disconnect();
			for (CommunicationStateListener listener : listeners) {
				listener.conn_error();
			}
		}
		logger.debug("Communication state changed to " + communicationState);
	}

	/**
	 * Clean up service to enable the communication to reestablish the
	 * connection. This is necessary to enable the Thread which detected the
	 * communication exeption to return.
	 * 
	 * @author Andreas Huebner - andreas@pramari.com
	 * 
	 */
	private class CleanUP implements Runnable {

		/**
		 * Exception occured
		 */
		private Exception exception;

		/**
		 * Constructor to create a new Cleanup Service Thread
		 * 
		 * @param exception
		 *            Exception occured on the physical communication layer
		 */
		public CleanUP(Exception exception) {
			logger.debug("Starting up clean service");
			this.exception = exception;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			// Give Read and Write Thread time to react
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			logger.debug("Cleaning up connection by disconnecting");
			// Tell the connection object about the failed physical connection
			connection.setException(exception);

			// Clean up the connection by closing all handles and threads
			disconnect();

			// try to reconnect the communication
			logger.debug("Try to reconnect again");
			try {
				reconnect();
			} catch (RifidiConnectionException e1) {
				changeState(ConnectionStatus.ERROR);
			}
			logger.debug("ending cleanup service");
			// Security lock so that only one CleanUp Service Thread can run at
			// the same time
			cleanUP = false;
		}

	}
}
