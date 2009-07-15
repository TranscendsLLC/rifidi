/**
 * 
 */
package org.rifidi.edge.readerplugin.alien.autonomous;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.core.sensors.base.AbstractSensor;
import org.rifidi.edge.core.sensors.base.AbstractSensorSession;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * The Session that Alien Readers can send reports to. When the session is
 * started, it opens up a serversocket and uses the executor pattern to handle
 * each new request.
 * 
 * It is not possible to submit commands to this session, since it is passive.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienAutonomousSensorSession extends AbstractSensorSession {

	/** port of server socket */
	private int serverSocketPort;
	/** The executor service that executes all Handlers */
	private ThreadPoolExecutor executorService;
	/** The logger */
	private final static Log logger = LogFactory
			.getLog(AlienAutonomousSensorSession.class);
	/** The server socket */
	private ServerSocket serverSocket;
	/** The notifierService used to send out notifications of session changes */
	private NotifierService notifierService;
	private int maxNumAutonomousReaders;

	/**
	 * Constructor
	 * 
	 * @param sensor
	 *            The sensor that this session connects
	 * @param ID
	 *            The ID of this session
	 * @param template
	 *            The template used to send tags
	 * @param NotifierService
	 *            Service used to send notifications to clients
	 * @param serverSocketPort
	 *            The port to bind to
	 */
	public AlienAutonomousSensorSession(AbstractSensor<?> sensor, String ID,
			JmsTemplate template, NotifierService notifierService,
			int serverSocketPort, int maxNumAutonomousReaders) {
		super(sensor, ID, template.getDefaultDestination(), template);
		this.serverSocketPort = serverSocketPort;
		this.notifierService = notifierService;
		this.maxNumAutonomousReaders = maxNumAutonomousReaders;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#connect()
	 */
	@Override
	public void connect() throws IOException {
		if (getStatus() == SessionStatus.CONNECTING
				|| getStatus() == SessionStatus.PROCESSING) {
			logger.warn("Session already started");
			return;
		}
		setStatus(SessionStatus.CONNECTING);

		// create a new executor service that will handle each new request. Use
		// SynchronousQueue so that tasks will be rejected if there is no free
		// worker thread available
		executorService = new ThreadPoolExecutor(0, maxNumAutonomousReaders, 5,
				TimeUnit.MINUTES, new SynchronousQueue<Runnable>());

		// open up server socket
		try {
			serverSocket = new ServerSocket(serverSocketPort);
		} catch (IOException e) {
			logger.error("Cannot start Alient Autonomous Sensor");
			disconnect();
			throw e;
		}
		logger.info("Alien Autonomous Sensor listening on port "
				+ serverSocket.getLocalPort()
				+ ".  Maximum number of concurrent readers supported: "
				+ maxNumAutonomousReaders);
		setStatus(SessionStatus.PROCESSING);

		// while session is not closed, process each request
		while (!executorService.isShutdown()) {
			try {
				// wait on a new request
				Socket clientSocket = serverSocket.accept();
				logger.info("Accepted client at "
						+ clientSocket.getInetAddress());

				// give the socket to the handler to do the dirty work
				AlienAutonomousReadThread handler = new AlienAutonomousReadThread(
						clientSocket, this, this.getTemplate());
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
		logger.info("Stopping Alien Autonomous Sensor Session");
		setStatus(SessionStatus.CREATED);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.SensorSession#disconnect()
	 */
	@Override
	public void disconnect() {
		// shutdown executor service, which calls interrupt() on all of the
		// handlers, causing them to shutdown properly
		executorService.shutdownNow();
		try {
			// close the socket, so we are not waiting in accept
			serverSocket.close();
		} catch (IOException e) {
			// ignore
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensorSession#setStatus(org
	 * .rifidi.edge.api.SessionStatus)
	 */
	@Override
	protected synchronized void setStatus(SessionStatus status) {
		super.setStatus(status);
		notifierService.sessionStatusChanged(super.getSensor().getID(),
				getID(), status);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensorSession#killComand(java
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
	 * org.rifidi.edge.core.sensors.base.AbstractSensorSession#submit(org.rifidi
	 * .edge.core.sensors.commands.Command, long, java.util.concurrent.TimeUnit)
	 */
	@Override
	public Integer submit(Command command, long interval, TimeUnit unit) {
		logger.warn("Cannot submit a command to a passive session");
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.AbstractSensorSession#submit(org.rifidi
	 * .edge.core.sensors.commands.Command)
	 */
	@Override
	public void submit(Command command) {
		logger.warn("Cannot submit a command to a passive session");
	}

}
