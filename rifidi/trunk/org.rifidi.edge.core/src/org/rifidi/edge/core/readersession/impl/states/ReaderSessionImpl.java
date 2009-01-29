package org.rifidi.edge.core.readersession.impl.states;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Semaphore;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.api.readerplugin.commands.api.Command;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.api.readersession.enums.CommandStatus;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.communication.service.CommunicationStateListener;
import org.rifidi.edge.core.communication.service.ConnectionService;
import org.rifidi.edge.core.communication.service.ConnectionStatus;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.impl.CommandExecutionListener;
import org.rifidi.edge.core.readersession.impl.CommandJournal;
import org.rifidi.edge.core.readersession.impl.CommandWrapper;
import org.rifidi.edge.core.readersession.impl.ExecutionThread;
import org.rifidi.edge.core.readersession.impl.ReaderSessionState;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

/**
 * 
 * The ReaderSession handles execution of commands to one physical reader. It
 * listens to the Communication to figure out if a physical connection is
 * established. It also listens to the Command to figure out when the command
 * has finished
 * 
 * @author Andreas Huebner - Andreas@pramari.com
 * @author Jerry Maine - jerry@pramari.com
 * @author kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderSessionImpl implements ReaderSession, ReaderSessionState,
		CommunicationStateListener, CommandExecutionListener {

	private static final Log logger = LogFactory
			.getLog(ReaderSessionImpl.class);

	// Services
	private ConnectionService connectionService;
	private MessageService messageService;

	// Session Information
	protected ReaderPlugin plugin;
	protected ReaderInfo readerInfo;
	protected ConnectionManager connectionManager;
	protected Connection connection;
	protected MessageQueue messageQueue;
	protected MessageQueue errorQueue;

	// CommandExecution
	protected CommandWrapper curCommand;
	protected ExecutionThread executionThread;
	protected CommandJournal commandJournal = new CommandJournal();
	protected long commandID = 0;

	// Status
	protected ConnectionStatus connectionStatus = ConnectionStatus.DISCONNECTED;

	// Reader Session State
	private ReaderSessionState sessionState;
	private Semaphore transitionSem;

	Long readerSessionID;

	public ReaderSessionImpl(ReaderPlugin plugin, ReaderInfo readerInfo,
			Long readerSessionID) {
		this.readerInfo = readerInfo;
		this.readerSessionID = readerSessionID;
		this.plugin = plugin;
		this.connectionManager = plugin.newConnectionManager(readerInfo);
		ServiceRegistry.getInstance().service(this);
		transitionSem = new Semaphore(1, true);
		sessionState = new SessionStateConfigured(this);
	}

	/*
	 * =====================================================================
	 * READER SESSION METHODS
	 * =====================================================================
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#getSessionID()
	 */
	@Override
	public long getSessionID() {
		return this.readerSessionID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSession#executeCommand(java.
	 * lang.String, java.lang.String)
	 */
	@Override
	public ReaderSessionStatus getStatus() {
		return state_getStatus();
	}

	@Override
	public long executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException {

		return state_executeCommand(configuration);
	}

	@Override
	public PropertyConfiguration executeProperty(PropertyConfiguration propertiesToExecute, boolean set)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException,
			RifidiInvalidConfigurationException,
			RifidiCannotRestartCommandException {
		return this.state_executeProperty(propertiesToExecute, set);
	}

	protected void validate(Class<?> command, Element elementToValidate)
			throws SAXException, IOException {

		// TODO: worry about validating later
		/*
		 * SchemaFactory schemaFactory = SchemaFactory
		 * .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI); InputStream is =
		 * Resource.getResource(command, command.getSimpleName() + ".xsd");
		 * 
		 * if (is != null) { Schema schema = schemaFactory.newSchema(new
		 * StreamSource(is)); Validator v = schema.newValidator();
		 * v.validate(new DOMSource(elementToValidate)); } else { throw new
		 * IOException("XSD file could not be found: "); }
		 */

	}

	/**
	 * Create a connection
	 * 
	 * @throws RifidiConnectionException
	 *             if connection could not be established because there was a
	 *             problem with the connection manager
	 */
	protected void connect() throws RifidiConnectionException {
		if (connection == null) {
			connection = connectionService.createConnection(connectionManager,
					readerInfo, this);
		}
	}

	protected Object instantiate(CommandDescription commandDescription)
			throws RifidiCommandNotFoundException {

		Object obj = null;
		try {
			obj = Class.forName(commandDescription.getClassname())
					.getConstructor().newInstance();
		} catch (IllegalArgumentException e) {
			throw new RifidiCommandNotFoundException(e.getMessage());
		} catch (SecurityException e) {
			throw new RifidiCommandNotFoundException(e.getMessage());
		} catch (InstantiationException e) {
			throw new RifidiCommandNotFoundException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RifidiCommandNotFoundException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new RifidiCommandNotFoundException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new RifidiCommandNotFoundException(e.getMessage());
		} catch (ClassNotFoundException e) {
			throw new RifidiCommandNotFoundException(e.getMessage());
		}
		return obj;
	}

	@Override
	public void enable() {
		this.state_enable();

	}

	@Override
	public void disable() {
		this.state_disable();

	}

	// TODO: figure out return value
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSession#stopCurCommand(boolean)
	 */
	@Override
	public void stopCurCommand(boolean force) {
		this.state_stopCommand(force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSession#stopCurCommand(boolean,
	 * long)
	 */
	@Override
	public void stopCurCommand(boolean force, long commandID) {
		if (commandID == this.commandID) {
			stopCurCommand(force);
		}
	}

	// TODO: what should this return if we are not executing anything?
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSession#curExecutingCommand()
	 */
	@Override
	public String curExecutingCommand() {
		if (curCommand != null)
			return curCommand.getCommandName();
		else
			return "NO CURRENT COMMAND";
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.ReaderSession#commandName()
	 */
	@Override
	public String commandName(long id) {
		return commandJournal.getCommandName(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSession#curExecutingCommandID()
	 */
	@Override
	public long curExecutingCommandID() {
		if (curCommand != null) {
			return curCommand.getCommandID();
		} else
			return -1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#commandStatus()
	 */
	@Override
	public CommandStatus commandStatus() {
		if (curCommand != null) {
			return curCommand.getCommandStatus();
		} else
			return CommandStatus.NOCOMMAND;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#commandStatus(long)
	 */
	@Override
	public CommandStatus commandStatus(long id) {
		return this.commandJournal.getCommandStatus(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSession#getMessageQueueName()
	 */
	@Override
	public String getMessageQueueName() {
		return this.messageQueue.getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readersession.ReaderSession#getReaderInfo()
	 */
	@Override
	public ReaderInfo getReaderInfo() {
		return readerInfo;
	}

	@Override
	public boolean setReaderInfo(ReaderInfo readerInfo) {
		return state_setReaderInfo(readerInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readersession.ReaderSession#resetReaderSession()
	 */
	@Override
	public void resetReaderSession() {
		state_resetSession();
	}

	/*
	 * =====================================================================
	 * Command Execution Event
	 * =====================================================================
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.impl.CommandExecutionListener#
	 * commandFinished(org.rifidi.edge.core.api.readerplugin.commands.Command,
	 * org.rifidi.edge.core.api.readerplugin.commands.CommandReturnStatus)
	 */
	@Override
	public void commandFinished(Command command, CommandReturnStatus status) {
		state_commandFinished();
	}

	/*
	 * =====================================================================
	 * Connection Event Listener
	 * =====================================================================
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.communication.service.CommunicationStateListener
	 * #connected()
	 */
	@Override
	public void conn_connected() {
		sessionState.conn_connected();
	}

	@Override
	public void conn_disconnected() {
		sessionState.conn_disconnected();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.communication.service.CommunicationStateListener
	 * #error()
	 */
	@Override
	public void conn_error() {
		sessionState.conn_error();
	}

	/*
	 * =====================================================================
	 * Service Injection Framework
	 * =====================================================================
	 */

	/**
	 * Injecting method for obtaining a instance of the ConnectionService
	 * through the ServiceRegistry Framework
	 * 
	 * @param connectionService
	 *            instance of the ConnectionService
	 */
	@Inject
	public void setConnectionService(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}

	/**
	 * Injection method to obtain a instance of the MessageService through the
	 * ServiceRegistry Framework
	 * 
	 * @param messageService
	 *            instance of the MessageService
	 */
	@Inject
	public void setMessageService(MessageService messageService) {
		this.messageService = messageService;
		logger.debug("Creating message queues for " + readerSessionID);
		this.messageQueue = this.messageService.createMessageQueue(Long
				.toString(readerSessionID));
		this.errorQueue = this.messageService.createMessageQueue("E"
				+ Long.toString(readerSessionID));
	}

	/*
	 * =====================================================================
	 * CleanUp for Instance destroy
	 * =====================================================================
	 */

	/**
	 * This method must be called to clean up when the session is deleted
	 */
	public void destroy() {
		logger.debug("Destroying ReaderSession: " + this.readerSessionID);
		this.messageService.destroyMessageQueue(this.messageQueue);
		this.messageService.destroyMessageQueue(this.errorQueue);
		cleanUpConnection();
		sessionState = null;
	}

	/**
	 * This method destroys the connection for this reader session. It should be
	 * called when something is wrong with the physical connection or when
	 * deleting this session all together
	 */
	protected void cleanUpConnection() {
		try {
			if (connection != null) {
				connectionService.destroyConnection(connection, this);
			}
		} catch (RuntimeException e) {
			logger.debug("Got exception: ", e);
			throw e;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "READER SESSION: " + this.readerSessionID + " TYPE: "
				+ this.readerInfo.getClass().getSimpleName();

	}

	/*
	 * =====================================================================
	 * SessionStates Transitions
	 * =====================================================================
	 */

	@Override
	public void state_enable() {
		try {
			transitionSem.acquire();
		} catch (InterruptedException e) {
		}
		sessionState.state_enable();

	}

	@Override
	public void state_disable() {
		try {
			transitionSem.acquire();
		} catch (InterruptedException e) {
		}
		sessionState.state_disable();

	}

	@Override
	public void state_commandFinished() {
		/*
		 * try { transitionSem.acquire(); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		sessionState.state_commandFinished();

	}

	@Override
	public void state_error() {
		try {
			transitionSem.acquire();
		} catch (InterruptedException e) {
		}
		sessionState.state_error();

	}

	@Override
	public long state_executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException{
		try {
			transitionSem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sessionState.state_executeCommand(configuration);
	}

	@Override
	public PropertyConfiguration state_executeProperty(PropertyConfiguration propertiesToExecute,
			boolean set) throws RifidiConnectionException,
			RifidiCommandNotFoundException, RifidiCommandInterruptedException,
			RifidiCannotRestartCommandException, RifidiInvalidConfigurationException {
		try {
			transitionSem.acquire();
		} catch (InterruptedException e) {
		}
		return sessionState.state_executeProperty(propertiesToExecute, set);

	}

	@Override
	public void state_propertyFinished()
			throws RifidiCannotRestartCommandException {
		/*
		 * try { transitionSem.acquire(); } catch (InterruptedException e) { //
		 * TODO Auto-generated catch block e.printStackTrace(); }
		 */
		sessionState.state_propertyFinished();

	}

	@Override
	public void state_resetSession() {
		try {
			transitionSem.acquire();
		} catch (InterruptedException e) {
		}
		sessionState.state_resetSession();

	}

	@Override
	public void state_stopCommand(boolean force) {
		try {
			transitionSem.acquire();
		} catch (InterruptedException e) {
		}
		sessionState.state_stopCommand(force);

	}

	@Override
	public ReaderSessionStatus state_getStatus() {
		return sessionState.state_getStatus();
	}

	@Override
	public boolean state_setReaderInfo(ReaderInfo readerInfo) {
		try {
			transitionSem.acquire();
		} catch (InterruptedException e) {
		}
		return sessionState.state_setReaderInfo(readerInfo);

	}

	public void transition(ReaderSessionState newState) {
		this.sessionState = newState;
		transitionSem.release();
	}

}
