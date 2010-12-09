/*
 * 
 * AbstractServerSocketSensorSession.java
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
package org.rifidi.edge.sensors.sessions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.Command;
import org.rifidi.edge.sensors.sessions.ServerSocketSensorSessionReadThread;

/**
 * When the session is started, it opens up a serversocket and uses the executor
 * pattern to handle each new request.
 * 
 * It is not possible to submit commands to this session, since it is passive.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractServerSocketSensorSession extends
		AbstractSensorSession {

	/** The port for the server socket to listen on */
	private int serverSocketPort;
	/** The manximum number of concurrent clients supported */
	private int maxNumSensors;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(AbstractServerSocketSensorSession.class);
	/** The executor service that executes all Handlers */
	private ThreadPoolExecutor executorService;
	/** The actual server socket */
	private ServerSocket serverSocket;

	/***
	 * Constructor
	 * 
	 * @param sensor
	 *            The sensor this session works for
	 * @param ID
	 *            The ID of the session
	 * @param destination
	 *            The JMS destination
	 * @param template
	 *            The JMS template used for internal message bus
	 * @param serverSocketPort
	 *            The port of the socket
	 * @param maxNumSensors
	 *            The maximum number of concurrent clients supported
	 * @param commandConfigurations
	 */
	public AbstractServerSocketSensorSession(AbstractSensor<?> sensor,
			String ID, int serverSocketPort, int maxNumSensors,
			Set<AbstractCommandConfiguration<?>> commandConfigurations) {
		super(sensor, ID,commandConfigurations);
		this.serverSocketPort = serverSocketPort;
		this.maxNumSensors = maxNumSensors;
	}

	/**
	 * Return a MessageParsingStrategyFactory for this session to use
	 * 
	 * @return
	 */
	protected abstract MessageParsingStrategyFactory getMessageParsingStrategyFactory();

	/***
	 * Return a MessageProcessingStrategyFactory for this session to use
	 * 
	 * @return
	 */
	protected abstract MessageProcessingStrategyFactory getMessageProcessingStrategyFactory();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#connect()
	 */
	@Override
	protected void _connect() throws IOException {
		if(serverSocketPort<1){
			logger.info("Not starting server socket, since port < 1");
			return;
		}
		
		if (getStatus() == SessionStatus.CONNECTING
				|| getStatus() == SessionStatus.PROCESSING) {
			logger.warn("Session already started");
			return;
		}
		setStatus(SessionStatus.CONNECTING);

		// create a new executor service that will handle each new request. Use
		// SynchronousQueue so that tasks will be rejected if there is no free
		// worker thread available
		executorService = new ThreadPoolExecutor(0, maxNumSensors, 5,
				TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

		// open up server socket
		try {
			serverSocket = new ServerSocket(serverSocketPort);
		} catch (IOException e) {
			logger.error("Cannot start Alient Autonomous Sensor");
			disconnect();
			throw e;
		}
		logger.info(sensor.getID() + " listening on port "
				+ serverSocket.getLocalPort()
				+ ".  Maximum number of concurrent readers supported: "
				+ maxNumSensors);
		setStatus(SessionStatus.PROCESSING);

		// while session is not closed, process each request
		while (!executorService.isShutdown()) {
			try {
				// wait on a new request
				Socket clientSocket = serverSocket.accept();
				logger.info("Accepted client at "
						+ clientSocket.getInetAddress());

				// give the socket to the handler to do the dirty work
				ServerSocketSensorSessionReadThread handler = new ServerSocketSensorSessionReadThread(
						clientSocket, getMessageParsingStrategyFactory(),
						getMessageProcessingStrategyFactory());
				try {
					executorService.execute(handler);
				} catch (RejectedExecutionException e) {
					logger.warn("Cannot create a handler thread for socket "
							+ clientSocket.getInetAddress(), e);
					clientSocket.close();
				}
			} catch (IOException e) {
				// print an error message if we weren't the ones who caused the
				// problem
				if (!executorService.isShutdown()) {
					logger.error("Failed to start Alien Autonomous Handler", e);
				}
			}
		}
		logger.info("Stopping " + sensor.getID());
		setStatus(SessionStatus.CREATED);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.SensorSession#disconnect()
	 */
	@Override
	public void disconnect() {

		// shutdown executor service, which calls interrupt() on all of the
		// handlers, causing them to shutdown properly
		if (executorService != null) {
			executorService.shutdownNow();
		}
		try {
			// close the socket, so we are not waiting in accept
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e) {
			// ignore
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensorSession#killComand(java
	 * .lang.Integer)
	 */
	@Override
	public void killComand(Integer id) {
		logger.warn("Cannot kill a command for a passive session");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.base.AbstractSensorSession#submit(java.lang
	 * .String, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(String commandID, long interval, TimeUnit unit) {
		logger.warn("Cannot submit a command to a passive session");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.sessions.AbstractSensorSession#submit(org
	 * .rifidi.edge.core.sensors.commands.Command)
	 */
	@Override
	public void submit(Command command) {
		logger.warn("Cannot submit a command to a passive session");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.sensors.SensorSession#restoreCommands(org.rifidi
	 * .edge.api.rmi.dto.SessionDTO)
	 */
	@Override
	public void restoreCommands(SessionDTO dto) {
		logger.info("Cannot restore commands on a passive session");
	}
	
	@Override
	public String toString() {
		return "IPServerSession: " + serverSocketPort + " (" + getStatus() + ")";
	}

}
