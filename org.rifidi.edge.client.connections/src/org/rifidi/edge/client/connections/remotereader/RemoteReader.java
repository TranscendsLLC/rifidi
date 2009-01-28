package org.rifidi.edge.client.connections.remotereader;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Element;
import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.exceptions.CannotExecutePropertyException;
import org.rifidi.edge.client.connections.exceptions.RifidiInvalidReaderInfoException;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.edge.client.connections.remotereader.listeners.ReaderMessageListener;
import org.rifidi.edge.client.connections.remotereader.listeners.ReaderStateListener;
import org.rifidi.edge.client.connections.util.JMSConsumerFactory;
import org.rifidi.edge.common.utilities.expiration.ExpiringHashMap;
import org.rifidi.edge.common.utilities.expiration.ExpiringObject;
import org.rifidi.edge.core.api.exceptions.RifidiException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.CommandInfo;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.ReaderSessionProperties;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.FormAnnotationListWrapper;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionCommandStatusCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionDisableCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionEnableCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionExecuteCommandCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionGetPropertyCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionGetReaderInfoCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionGetStateCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionResetCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionServerDescription;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionSetPropertyCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionSetReaderInfoCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionStopCurCommandCall;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.rmi.utils.cache.ServerDescription;
import org.rifidi.rmi.utils.cache.util.RemoteStubCacheUtil;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class represents a Remote Reader Session to the rest of the UI
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteReader implements MessageListener {

	/**
	 * The logger for this class
	 */
	private Log logger = LogFactory.getLog(RemoteReader.class);

	/**
	 * The factory for creating a new JMS Queue
	 */
	private JMSConsumerFactory jmsFactory;

	/**
	 * Set of messageListeners
	 */
	private Set<ReaderMessageListener> messageListeners = new HashSet<ReaderMessageListener>();

	/**
	 * Set of listners that care about changes to the session state
	 */
	private Set<ReaderStateListener> readerStateListeners = new HashSet<ReaderStateListener>();

	/**
	 * The message consumer for this reader
	 */
	private MessageConsumer consumer;

	/**
	 * The ReaderInfo for this reader
	 */
	private ReaderInfoWrapper readerInfoWrapper;

	/**
	 * The reader plugin wrapper provides some basic information about the kind
	 * of reader
	 */
	private ReaderPluginWrapper readerPluginWrapper;

	/**
	 * The id for this RemoteReader
	 */
	private RemoteReaderID remoteReaderID;

	/**
	 * The edge server connection which this reader belongs to
	 */
	private int serverID;

	/**
	 * The Annotations for the properties of this Reader Session
	 */
	private FormAnnotationListWrapper propertyAnnotationXMLWrapper;

	/**
	 * The Annotations for the commands of this Reader Session
	 */
	private FormAnnotationListWrapper commandAnnotationXMLWrapper;

	/**
	 * The current state of this reader session. It expires every second.
	 */
	private ExpiringObject<String> remoteReaderState = new ExpiringObject<String>(
			"", 0);

	/**
	 * The description of the RMI stub
	 */
	private SessionServerDescription sessionSD;

	/**
	 * The registry where we can look up the EdgeServerConnection
	 */
	private EdgeServerConnectionRegistryService ESRegistry;

	private ReaderSessionProperties readerSessionProperties;

	/**
	 * This hashmap caches values of PropertyConfiguration for one second
	 */
	private ExpiringHashMap<String, CommandConfiguration> propertyValueCache = new ExpiringHashMap<String, CommandConfiguration>(
			2000);

	/**
	 * 
	 * @param readerInfo
	 * @param jmsFactory
	 * @param readerSessionProperties
	 * @param remoteReaderID
	 * @param serverID
	 * @param serverDesc
	 */
	public RemoteReader(JMSConsumerFactory jmsFactory,
			ReaderSessionProperties readerSessionProperties,
			RemoteReaderID remoteReaderID, int serverID,
			ServerDescription serverDesc) {
		ServiceRegistry.getInstance().service(this);
		logger.debug(this.getClass().getName() + ": created");
		this.jmsFactory = jmsFactory;
		this.remoteReaderID = remoteReaderID;
		this.serverID = serverID;
		this.readerSessionProperties = readerSessionProperties;
		try {
			consumer = jmsFactory.createConsumer(readerSessionProperties);
			consumer.setMessageListener(this);
		} catch (JMSException e) {
			consumer = null;
			logger.error("JMS Consumer not created", e);
		}

		this.sessionSD = new SessionServerDescription(serverDesc.getServerIP(),
				serverDesc.getServerPort(), remoteReaderID.getRemoteSessionID());
	}

	/**
	 * @return the ID of this session
	 */
	public RemoteReaderID getID() {
		return this.remoteReaderID;
	}

	/**
	 * @return The ID of the server this session belongs to
	 */
	public int getServerID() {
		return this.serverID;
	}

	/**
	 * 
	 * @ return the reader name and the reader ID
	 */
	public String getDescription() {
		return this.getReaderPluginWrapper().getReaderName() + "-"
				+ this.remoteReaderID;
	}

	/**
	 * 
	 * @return the qualified class name of the readerInfo class of this reader's
	 *         plugin type
	 */
	public String getReaderInfoClassName() {
		return getReaderPluginWrapper().getReaderInfoClassName();
	}

	/**
	 * This method gets the ReaderInfo for this session. Note that this method
	 * involves a network call because the readerInfo could be changed by
	 * another client
	 * 
	 * @return The ReaderInfo for this session
	 */
	public ReaderInfoWrapper getReaderInfo() {
		SessionGetReaderInfoCall call = new SessionGetReaderInfoCall(sessionSD);
		ReaderInfoWrapper wrapper;
		try {
			wrapper = call.makeCall();
		} catch (ServerUnavailable e) {
			// TODO: clean up this reader session
			logger.error(e);
			return this.readerInfoWrapper;
		} catch (RifidiReaderInfoNotFoundException e) {
			logger.error(e);
			return this.readerInfoWrapper;
		}

		this.readerInfoWrapper = wrapper;
		return wrapper;

	}

	/**
	 * Set the reader info for this reader plugin
	 * 
	 * @param xml
	 *            The new readerInfo
	 * @throws RifidiInvalidReaderInfoException
	 *             If there was a problem when setting the plugin
	 */
	public void setReaderInfo(String xml)
			throws RifidiInvalidReaderInfoException {
		SessionSetReaderInfoCall call = new SessionSetReaderInfoCall(
				this.sessionSD, xml);
		try {
			call.makeCall();
		} catch (RifidiReaderInfoNotFoundException e) {
			throw new RifidiInvalidReaderInfoException();
		} catch (ServerUnavailable e) {
			// TODO: clean up this reader session
			logger.error(e);
		}
	}

	/**
	 * This method gets the ReaderPluginXML from the reader session. It involves
	 * an RMI call only the first time it is made during the life of this
	 * session
	 * 
	 * @return the ReaderPluginWrapper
	 */
	public ReaderPluginWrapper getReaderPluginWrapper() {
		// because the ReaderPluginWrapper stays the same for the life of the
		// session, only get it once from the server
		if (this.readerPluginWrapper == null) {
			EdgeServerConnection es = this.ESRegistry
					.getConnection(this.serverID);

			try {
				this.readerPluginWrapper = es
						.getReaderPluginWrapper(readerSessionProperties
								.getReaderInfoClassName());
			} catch (RifidiReaderPluginXMLNotFoundException e) {
				// TODO: should probably clean up all sessions that are a part
				// of this plugin
				logger.error(e);
			} catch (ServerUnavailable e) {
				// TODO: need to clean up the Edge server connection
				logger.error(e);
			}
		}
		return this.readerPluginWrapper;
	}

	/**
	 * Get current reader state
	 * 
	 * @return The current reader session state
	 */
	public synchronized String getReaderState() {
		if (remoteReaderState.getIfNotExpired() == null) {
			SessionGetStateCall call = new SessionGetStateCall(this.sessionSD);
			try {
				String newState = call.makeCall();
				String oldState = this.remoteReaderState.getObject();
				if (!newState.equalsIgnoreCase(oldState)) {
					for (ReaderStateListener l : this.readerStateListeners) {
						l.readerStateChanged(this.remoteReaderID, newState);
					}
				}
				this.remoteReaderState = new ExpiringObject<String>(newState,
						1000);
			} catch (ServerUnavailable e) {
				// TODO: should probably clean up this session
				logger.error(e);
				this.remoteReaderState = new ExpiringObject<String>(
						"UNAVAILABLE", 1000);
			}
		}
		return this.remoteReaderState.getObject();
	}

	/**
	 * Move the reader session from Configured to OK state
	 */
	public void enable() {
		SessionEnableCall call = new SessionEnableCall(this.sessionSD);
		try {
			call.makeCall();
		} catch (ServerUnavailable e) {
			// TODO should clean up session
			logger.error(e);
		}
	}

	/**
	 * Move the reader session to the configured state
	 */
	public void disable() {
		SessionDisableCall call = new SessionDisableCall(this.sessionSD);
		try {
			call.makeCall();
		} catch (ServerUnavailable e) {
			// TODO should clean up session
			logger.error(e);
		}
	}

	/**
	 * Move the reader session from the error to the configured state
	 */
	public void reset() {
		SessionResetCall call = new SessionResetCall(this.sessionSD);
		try {
			call.makeCall();
		} catch (ServerUnavailable e) {
			// TODO clean up session
			logger.error(e);
		}
	}

	/**
	 * Get the names of the properties in a certain group
	 * 
	 * @param group
	 * @return
	 */
	public Set<String> getPropertyNames(String group) {
		ReaderPluginWrapper wrapper = getReaderPluginWrapper();
		return wrapper.getPropertyNames(group);
	}

	/**
	 * Get the property annotations for a certain group
	 * 
	 * @param group
	 * @return
	 */
	public List<Element> getPropertyAnnotations(String group) {
		// because the PropertyAnnotations stay the same
		// for the lifetime of the reader, only get them once
		if (this.propertyAnnotationXMLWrapper == null) {
			EdgeServerConnection es = this.ESRegistry.getConnection(serverID);

			try {
				this.propertyAnnotationXMLWrapper = es
						.getReaderPropertyAnnotations(readerSessionProperties
								.getReaderInfoClassName());
			} catch (ServerUnavailable e) {
				// TODO: should probably clean up all sessions that are a part
				// of this plugin
				logger.error(e);
			} catch (RifidiException e) {
				// TODO: need to clean up the Edge server connection
				logger.error(e);
			}
		}

		if (propertyAnnotationXMLWrapper != null) {
			List<Element> propertyAnnotations = new ArrayList<Element>();
			ReaderPluginWrapper plugin = getReaderPluginWrapper();
			for (String s : plugin.getPropertyNames(group)) {
				Element e = propertyAnnotationXMLWrapper.getFormAnnotation(s);
				if (e == null) {
					logger.debug("No annotation with form name: " + s);
				} else {
					propertyAnnotations.add(propertyAnnotationXMLWrapper
							.getFormAnnotation(s));
				}
			}
			return propertyAnnotations;
		}
		return new ArrayList<Element>();
	}

	/**
	 * Return true if the session is in a state in which it can get or set
	 * properties
	 * 
	 * @return
	 */
	public boolean canExecuteProperty() {

		String state = getReaderState();
		if (ReaderSessionStatus.OK.toString().equalsIgnoreCase(state)
				|| ReaderSessionStatus.EXECUTING_COMMAND.toString()
						.equalsIgnoreCase(state)) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * This method looks up the values of properties on the server.
	 * 
	 * @param propertyNames
	 *            The names of the properties to lookup
	 * @return a PropertyConfiguration that contains the values for the
	 *         properties
	 * @throws CannotExecutePropertyException
	 */
	public synchronized PropertyConfiguration getProperties(
			Set<String> propertyNames) throws CannotExecutePropertyException {

		// This is the set of returnvalues that match up to the set of
		// propertyNames
		Set<CommandConfiguration> returnValues = new HashSet<CommandConfiguration>();

		// Check to see if the value is in the expiring cache
		Set<String> groupsToLookup = new HashSet<String>();
		for (String propertyName : propertyNames) {
			CommandConfiguration c = this.propertyValueCache.get(propertyName);
			if (c == null) {
				// if the property is not in the cache, add its group to be
				// lookedup
				groupsToLookup.addAll(getReaderPluginWrapper().getGroups(
						propertyName));
			} else {
				// otherwise add it to the list of return values
				returnValues.add(c);
			}
		}

		// if we need to make a remote call to update the cache for a property
		// group
		if (groupsToLookup.size() > 0) {

			// add the property names in each group to the list
			Set<String> propertiesToLookup = new HashSet<String>();
			for (String group : groupsToLookup) {
				propertiesToLookup.addAll(getReaderPluginWrapper()
						.getPropertyNames(group));
			}

			// form the call
			SessionGetPropertyCall call = new SessionGetPropertyCall(sessionSD,
					propertiesToLookup);
			try {

				// make the call
				PropertyConfiguration propConfig = call.makeCall();
				for (String propName : propConfig.getPropertyNames()) {
					CommandConfiguration cc = propConfig.getProperty(propName);

					// if the property was in the original list of requested
					// properties, add it to the return values
					if (propertyNames.contains(propName)) {
						returnValues.add(cc);
					}

					// put the property in the cache
					this.propertyValueCache.put(propName, cc);
				}
			} catch (ServerUnavailable e) {
				// TODO: should clean up RemoteReader here
				logger.error("Server Unavialble", e);
				throw new CannotExecutePropertyException();
			} catch (RifidiException e) {
				logger.error("Server Unavialble", e);
				throw new CannotExecutePropertyException();
			}
		}
		return new PropertyConfiguration(returnValues);
	}

	/**
	 * This method sets the properties on a reader session
	 * 
	 * @param propertyConfiguration
	 * @return
	 * @throws CannotExecutePropertyException
	 */
	public PropertyConfiguration setProperties(
			PropertyConfiguration propertyConfiguration)
			throws CannotExecutePropertyException {
		SessionSetPropertyCall call = new SessionSetPropertyCall(sessionSD,
				propertyConfiguration);
		try {
			return call.makeCall();
		} catch (ServerUnavailable e) {
			// TODO: should clean up RemoteReader here
			logger.error(e);
			throw new CannotExecutePropertyException();
		} catch (RifidiException e) {
			logger.error(e);
			throw new CannotExecutePropertyException();
		}

	}

	/**
	 * Get all available command names on the reader plugin
	 * 
	 * @return
	 */
	public Set<String> getCommandNames() {
		ReaderPluginWrapper plugin = getReaderPluginWrapper();
		return plugin.getCommandNames();
	}

	/**
	 * 
	 * @param commandName
	 * @return A JDOM element if found, null otherwise
	 */
	public Element getCommandAnnotation(String commandName) {
		if (this.commandAnnotationXMLWrapper == null) {
			EdgeServerConnection es = this.ESRegistry.getConnection(serverID);

			try {
				this.commandAnnotationXMLWrapper = es
						.getReaderCommandAnnotations(readerSessionProperties
								.getReaderInfoClassName());
			} catch (ServerUnavailable e) {
				// TODO: should probably clean up all sessions that are a part
				// of this plugin
				logger.error(e);
			} catch (RifidiException e) {
				// TODO: need to clean up the Edge server connection
				logger.error(e);
			}
		}

		if (commandAnnotationXMLWrapper != null) {
			return commandAnnotationXMLWrapper.getFormAnnotation(commandName);
		}
		return null;
	}

	/**
	 * Get the name of the currently executing command
	 * 
	 * @return
	 */
	public String currentlyExecutingCommand() {
		SessionCommandStatusCall call = new SessionCommandStatusCall(sessionSD);
		CommandInfo ci;
		try {
			ci = call.makeCall();
			return ci.getCommandName();
		} catch (ServerUnavailable e) {
			// TODO: should probably clean up this session
			logger.error(e);
		} catch (RuntimeException e) {
			logger.debug(e);
		}
		return "";
	}

	/**
	 * Return true if this session is in a state in which it can execute a
	 * property
	 * 
	 * @return
	 */
	public boolean canExecuteCommand() {

		String state = getReaderState();
		if (ReaderSessionStatus.OK.toString().equalsIgnoreCase(state)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Execute a command on the server
	 * 
	 * @param commandConfiguration
	 * @return The command ID of the newly executed command
	 */
	public long executeCommand(CommandConfiguration commandConfiguration) {
		SessionExecuteCommandCall call = new SessionExecuteCommandCall(
				this.sessionSD, commandConfiguration);
		try {
			return call.makeCall();
		} catch (ServerUnavailable e) {
			// TODO: clean up remote reader
			logger.error(e);
		} catch (RifidiException e) {
			logger.error(e);
		}
		return -1;
	}

	/**
	 * Stop the currenly executing command
	 */
	public void stopCommand() {
		SessionStopCurCommandCall call = new SessionStopCurCommandCall(
				sessionSD, true);
		try {
			call.makeCall();
		} catch (ServerUnavailable e) {
			// TODO: clean up remote reader
			logger.error(e);
		}
	}

	@Override
	public void onMessage(Message message) {
		logger.debug(message + ": in onMessageMethod");
		synchronized (messageListeners) {

			for (ReaderMessageListener messageListener : messageListeners) {
				messageListener.onMessage(message, this);
			}

			messageListeners.notifyAll();
		}
	}

	/**
	 * This method is used to clean up the Remote Reader. It should be called
	 * before the deleteSession remote call is made to the server, because this
	 * method destroys the JMS queue, which must be done before the session can
	 * be deleted on the server. It does not remove the reader from the from the
	 * registry service
	 */
	public void destroy() {
		messageListeners.clear();
		readerStateListeners.clear();

		// TODO: this totally breaks the design of a transparent cache
		RemoteStubCacheUtil.remove(this.sessionSD);
		try {
			jmsFactory.deleteConsumer(consumer);
		} catch (JMSException e) {
			logger.error("cannot delete consumer:", e);
		}
	}

	public void addMessageListener(ReaderMessageListener listener) {
		synchronized (messageListeners) {
			messageListeners.add(listener);
			messageListeners.notifyAll();
		}
	}

	public void removeMessageListener(ReaderMessageListener listener) {
		synchronized (messageListeners) {
			messageListeners.remove(listener);
			messageListeners.notifyAll();
		}
	}

	public void addStateListener(ReaderStateListener listener) {
		readerStateListeners.add(listener);
	}

	public void removeStateListener(ReaderStateListener listener) {
		readerStateListeners.remove(listener);
	}

	@Inject
	public void setEdgeServerRegistryService(
			EdgeServerConnectionRegistryService service) {
		this.ESRegistry = service;
	}

}
