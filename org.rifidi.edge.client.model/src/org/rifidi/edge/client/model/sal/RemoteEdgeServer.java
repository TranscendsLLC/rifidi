package org.rifidi.edge.client.model.sal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.TimeUnit;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.management.AttributeList;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.fosstrak.tdt.TDTEngine;
import org.rifidi.edge.client.model.SALModelPlugin;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.client.model.sal.preferences.EdgeServerPreferences;
import org.rifidi.edge.core.api.rmi.exceptions.CommandSubmissionException;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCCreateCommandConfiguration;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCDeleteCommandConfiguration;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCServerDescription;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESSave;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESServerDescription;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_CreateReader;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_CreateSession;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_DeleteReader;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_DeleteSession;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_KillCommand;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_StartSession;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_StopSession;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_SubmitCommand;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_SubmitSingleShotCommand;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * The Edge Server Model Object
 * 
 * @author Kyle Neumeier
 */
public class RemoteEdgeServer {

	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(RemoteEdgeServer.class);
	/** The set of reader factories on the server */
	ObservableMap readerFactories;
	/** The remote readers on this edge server */
	ObservableMap remoteReaders;
	/** Remote commandConfigurationFactories */
	ObservableMap commandConfigFactories;
	/** Remote CommandConfigurations */
	ObservableMap commandConfigurations;
	/** The current state of the edge server */
	private RemoteEdgeServerState state;
	/** The JMS notification object */
	ActiveMQConnectionFactory connectionFactory;
	/** The JMS Connection Object */
	Connection conn;
	/** The JMS session object */
	Session session;
	/** The JMS destination */
	Destination dest;
	/** The JMS consumer */
	MessageConsumer consumer;
	/** Destination of tag events */
	Destination dest_tags;
	/** Consumer of tag events */
	MessageConsumer consumer_tags;
	/** The last recorded start time of the server */
	Long startupTime = 0l;
	/** An object that recieves all JMS notifications */
	JMSMessageHandler jmsMessageHandler;
	/** The property change support for this class */
	private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	/** The name of the state property */
	public static final String STATE_PROPERTY = "org.rifidi.edge.client.model.sal.RemoteEdgeServer.state";
	/** engine for converting tags */
	TDTEngine tdtEngine;

	/**
	 * Constructor
	 * 
	 * @param ip
	 *            The IP of the server
	 * @param port
	 *            The RMI port of the server
	 */
	public RemoteEdgeServer() {
		changeState(RemoteEdgeServerState.DISCONNECTED);

		readerFactories = new WritableMap();
		remoteReaders = new WritableMap();
		commandConfigFactories = new WritableMap();
		commandConfigurations = new WritableMap();

		connectionFactory = new ActiveMQConnectionFactory();
		try {
			this.tdtEngine = new TDTEngine();
		} catch (Exception e) {
			logger.warn("Cannot create TDTEngine");
		}
		Thread t = new Thread(RequestExecuterSingleton.getInstance());
		t.start();
	}

	/**
	 * Connect to the edge server
	 */
	public void connect() {
		if (this.state == RemoteEdgeServerState.CONNECTED) {
			logger.warn("Edge Server is already in the connected state!");
			return;
		}
		RequestExecuterSingleton.getInstance().scheduleRequest(
				new Command_Connect(this));
	}

	/**
	 * Update the model
	 * 
	 * @throws ServerUnavailable
	 *             If there was a problem when connecting to the server
	 */
	public void update() {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot update the Remote Edge Server when "
					+ "it is in the Disconnected State!");
			return;
		}
		RequestExecuterSingleton.getInstance().scheduleRequest(
				new Command_Update(this));

	}

	/**
	 * Disconnect from the edge server
	 */
	public void disconnect() {
		if (this.state == RemoteEdgeServerState.DISCONNECTED) {
			logger.warn("Remote Edge Server is already "
					+ "in the Disconnected State!");
			return;
		}

		RequestExecuterSingleton.getInstance().scheduleRequest(
				new Command_Disconnect(this));

	}

	/***
	 * Save the configuration on the server
	 */
	public void saveConfiguration() {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot save configuration when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}

		ESSave saveCommand = new ESSave(getESServerDescription());
		try {
			saveCommand.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while saving Remote Configuration", e);
			disconnect();
		}

	}

	/***
	 * Delete a reader from the server
	 * 
	 * @param readerID
	 *            The ID of the reader to delete
	 */
	public void deleteReader(String readerID) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot delete the reader when Edge Serve "
					+ "is in the Disconnecte State!");
			return;
		}
		RS_DeleteReader deleteCommand = new RS_DeleteReader(
				getRSServerDescription(), readerID);
		try {
			deleteCommand.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while deleting reader", e);
			disconnect();
		}
	}

	/**
	 * Create a new reader on the server
	 * 
	 * @param factory
	 *            The factory to user to crate the reader
	 * @param list
	 *            A list of attributes to set on the reader
	 */
	public void createReader(final RemoteReaderFactory factory,
			final AttributeList list) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot create Reader when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}
		if (factory == null || list == null) {
			logger.error("something is null" + factory + " " + list);
			return;
		}
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				RS_ServerDescription serverDescription = getRSServerDescription();
				RS_CreateReader call = new RS_CreateReader(serverDescription,
						factory.getID(), list);
				try {
					call.makeCall();
				} catch (ServerUnavailable e) {
					logger.error("Server Unavailable: ", e);
					disconnect();
				}
			}

		});
		t.start();
	}

	/***
	 * Create a session on a reader
	 * 
	 * @param readerID
	 *            The ID of the session
	 */
	public void createSession(String readerID) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot create session when Edge Server "
					+ "is in the Disconnecte State!");
			return;
		}
		RS_CreateSession createSession = new RS_CreateSession(
				getRSServerDescription(), readerID);
		try {
			createSession.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while deleting reader", e);
			disconnect();
		}
	}

	/**
	 * Delete a session on a reader
	 * 
	 * @param readerID
	 *            The ID of the reader that has the session to delete
	 * @param sessionID
	 *            The ID of the session
	 */
	public void deleteSession(String readerID, String sessionID) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot delete session when Edge Server "
					+ "is in the Disconnecte State!");
			return;
		}
		RS_DeleteSession deleteSession = new RS_DeleteSession(
				getRSServerDescription(), readerID, sessionID);
		try {
			deleteSession.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while deleting reader", e);
			disconnect();
		}

	}

	/***
	 * Start a session
	 * 
	 * @param readerID
	 *            The ID of the reader that has the session
	 * @param sessionID
	 *            The ID of the session to start
	 */
	public void startSession(String readerID, String sessionID) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot start session when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}
		RS_StartSession startSession = new RS_StartSession(
				getRSServerDescription(), readerID, sessionID);

		try {
			startSession.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while starting session", e);
			disconnect();
		}

	}

	/***
	 * Stop a session on the reader
	 * 
	 * @param readerID
	 *            The ID of the reader that has the session
	 * @param sessionID
	 *            The ID of the session to stop
	 */
	public void stopSession(String readerID, String sessionID) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot stop session when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}
		RS_StopSession stopSession = new RS_StopSession(
				getRSServerDescription(), readerID, sessionID);

		try {
			stopSession.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while stopping session", e);
			disconnect();
		}

	}

	public void createCommandConfiguration(String readerConfigurationType) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot create command configuration when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}
		CCCreateCommandConfiguration createCommand = new CCCreateCommandConfiguration(
				getCCServerDescription(), readerConfigurationType,
				new AttributeList());

		try {
			createCommand.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while creating command configuration ", e);
			disconnect();
		}

	}

	/**
	 * Deletes the command configuration with the given ID.
	 * 
	 * @param readerConfigurationID
	 */
	public void deleteCommandConfiguration(String readerConfigurationID) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot create command configuration when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}
		CCDeleteCommandConfiguration deleteCommand = new CCDeleteCommandConfiguration(
				getCCServerDescription(), readerConfigurationID);

		try {
			deleteCommand.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while deleting command configuration ", e);
			disconnect();
		}

	}

	/**
	 * Deletes the given job.
	 * 
	 * @param job
	 */
	public void deleteRemoteJob(RemoteJob job) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot delete remote job when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}
		RS_KillCommand kill = new RS_KillCommand(getRSServerDescription(), job
				.getReaderID(), job.getSessionID(), job.getJobID());
		try {
			kill.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while killing remote job ", e);
			disconnect();
		}

	}

	/**
	 * Submits a command to a session
	 * 
	 * @param session
	 *            The Session to schedule the command on
	 * @param configuration
	 *            the command to submit
	 * @param interval
	 *            the time repeat interval (in ms)
	 */
	public void scheduleJob(RemoteSession session,
			RemoteCommandConfiguration configuration, Long interval) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot schedule a job when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}
		RS_SubmitCommand command = new RS_SubmitCommand(
				getRSServerDescription(), session.getReaderID(), session
						.getSessionID(), configuration.getID(), interval,
				TimeUnit.MILLISECONDS);
		try {
			command.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while submitting remote job ", e);
			disconnect();
		} catch (CommandSubmissionException e) {
			logger.error("Exception while submitting remote job ", e);
		}
	}

	/**
	 * Submits a one-shot command to a session. The command only runs briefly
	 * and does not execute again
	 * 
	 * @param session
	 *            The session to send the coommand to
	 * @param configuration
	 *            The command to submit
	 */
	public void submitOneTimeCommand(RemoteSession session,
			RemoteCommandConfiguration configuration) {
		if (this.state != RemoteEdgeServerState.CONNECTED) {
			logger.warn("Cannot submit a one-time command when Edge Server "
					+ "is in the Disconnected State!");
			return;
		}
		RS_SubmitSingleShotCommand command = new RS_SubmitSingleShotCommand(
				getRSServerDescription(), session.getReaderID(), session
						.getSessionID(), configuration.getID());
		try {
			command.makeCall();
		} catch (ServerUnavailable e) {
			logger.error("Exception while submitting remote job ", e);
			disconnect();
		} catch (CommandSubmissionException e) {
			logger.error("Exception while submitting remote job ", e);
		}
	}

	/**
	 * This returns a references to the readerFactories map
	 * 
	 * @return the readerFactoryIDs
	 */
	public ObservableMap getReaderFactories() {
		return readerFactories;
	}

	/**
	 * This returns a reference to the remoteReaders map
	 * 
	 * @return the remoteReaders
	 */
	public ObservableMap getRemoteReaders() {
		return remoteReaders;
	}

	/**
	 * Returns the list of remote command configuration factories.
	 * 
	 * @return
	 */
	public ObservableMap getRemoteCommandConfigFactories() {
		return commandConfigFactories;
	}

	/**
	 * Returns a reference to the the map of RemoteCommandConfigurations
	 * 
	 * @return the commandConfigurations
	 */
	public ObservableMap getCommandConfigurations() {
		return commandConfigurations;
	}

	/**
	 * 
	 * @return The current state of the RemoteEdgeServer
	 */
	public RemoteEdgeServerState getState() {
		return this.state;
	}

	/**
	 * This method changes the state of the Remote Edge Server. It must be
	 * called from within an eclipse thread
	 * 
	 * @param newState
	 */
	synchronized void changeState(RemoteEdgeServerState newState) {
		logger.info("Remote Edge Server State: " + newState);
		RemoteEdgeServerState oldstate = this.state;
		this.state = newState;
		this.pcs.firePropertyChange(STATE_PROPERTY, oldstate, newState);
		logger.debug("Number of PropertyChangeListeners Notified "
				+ pcs.getPropertyChangeListeners().length);
	}

	/**
	 * Add a PropertyChangedListener
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remote a PropertyChangedListener
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	/**
	 * Helper method that looks up connection information in the Eclipse
	 * Property Store and returns a RMI stub description to communicate with a
	 * reader stub
	 * 
	 * @return The
	 */
	RS_ServerDescription getRSServerDescription() {
		String ip = SALModelPlugin.getDefault().getPreferenceStore().getString(
				EdgeServerPreferences.EDGE_SERVER_IP);
		int port = Integer.parseInt(SALModelPlugin.getDefault()
				.getPreferenceStore().getString(
						EdgeServerPreferences.EDGE_SERVER_PORT_RMI));
		return new RS_ServerDescription(ip, port);
	}

	/**
	 * Helper method that looks up connection information in the Eclipse
	 * Property Store and returns a RMI stub description to communicate with a
	 * edge server stub
	 * 
	 * @return
	 */
	ESServerDescription getESServerDescription() {
		String ip = SALModelPlugin.getDefault().getPreferenceStore().getString(
				EdgeServerPreferences.EDGE_SERVER_IP);
		int port = Integer.parseInt(SALModelPlugin.getDefault()
				.getPreferenceStore().getString(
						EdgeServerPreferences.EDGE_SERVER_PORT_RMI));
		return new ESServerDescription(ip, port);
	}

	/**
	 * Helper method that looks up connection information in the Eclipse
	 * Property Store and returns a RMI stub description to communicate with a
	 * edge server stub
	 * 
	 * @return
	 */
	CCServerDescription getCCServerDescription() {
		String ip = SALModelPlugin.getDefault().getPreferenceStore().getString(
				EdgeServerPreferences.EDGE_SERVER_IP);
		int port = Integer.parseInt(SALModelPlugin.getDefault()
				.getPreferenceStore().getString(
						EdgeServerPreferences.EDGE_SERVER_PORT_RMI));
		return new CCServerDescription(ip, port);
	}

}
