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

	public Communication(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
		protocol = this.connectionManager.getCommunicationProtocol();
		readQueue = new LinkedBlockingQueue<Object>();
		writeQueue = new LinkedBlockingQueue<Object>();
		connection = new ConnectionImpl(readQueue, writeQueue);
	}

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
		}
		return connection;
	}

	public void disconnect() {

		changeState(ConnectionStatus.DISCONNECTED);

		writeThread.ignoreExceptions(true);
		readThread.ignoreExceptions(true);

		connectionManager.disconnect(connection);

		writeThread.stop();
		readThread.stop();
	}

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
			connection = null;
			// status = ReaderSessionStatus.DISCONNECTED;
			throw new RifidiConnectionException("RuntimeException detected! "
					+ "There is a possible bug in "
					+ connectionManager.getClass().getName(), e);
		}
		/* fire events */
		if (x == maxConn) {
			throw new RifidiConnectionException("MAX_CONNECTION_ATTEMPS");
		} else {
			changeState(ConnectionStatus.CONNECTED);
		}
	}

	private void physicalConnect() throws RifidiConnectionException {
		logger.debug("physical connection attempted");
		ConnectionStreams connectionStreams = connectionManager
				.createCommunication();
		readThread = new ReadThread(connectionManager.toString()
				+ " Read Thread", this, protocol, readQueue, connectionStreams
				.getInputStream());

		writeThread = new WriteThread(connectionManager.toString()
				+ " Write Thread", this, protocol, writeQueue,
				connectionStreams.getOutputStream());
		readThread.start();
		writeThread.start();
	}

	public void addCommunicationStateListener(
			CommunicationStateListener listener) {
		listeners.add(listener);
	}

	public void removeCommunicationStateListener(
			CommunicationStateListener listener) {
		listeners.remove(listener);
	}

	/* ============================================================== */
	@Override
	public void finalize() {
		listeners.clear();
	}

	@Override
	public void connectionExceptionEvent(Exception exception) {
		logger.debug("Connection Exception Event");
		synchronized (this) {
			if (!cleanUP) {
				cleanUP = true;
				new Thread(new CleanUP(exception), "ReconnectingThread: " + connectionManager.toString())
						.start();
			}
		}
	}

	private void changeState(ConnectionStatus communicationState) {
		logger.debug("Communication state changed to " + communicationState);
		if (communicationState == ConnectionStatus.CONNECTED) {
			for (CommunicationStateListener listener : listeners) {
				listener.connected();
			}
		} else if (communicationState == ConnectionStatus.DISCONNECTED) {
			for (CommunicationStateListener listener : listeners) {
				listener.disconnected();
			}

		} else if (communicationState == ConnectionStatus.ERROR) {
			disconnect();
			for (CommunicationStateListener listener : listeners) {
				listener.error();
			}
		}
	}

	private class CleanUP implements Runnable {

		private Exception exception;

		public CleanUP(Exception exception) {
			this.exception = exception;
		}

		@Override
		public void run() {
			disconnect();
			connection.setException(exception);
			connect();
			cleanUP = false;
		}

	}
}
