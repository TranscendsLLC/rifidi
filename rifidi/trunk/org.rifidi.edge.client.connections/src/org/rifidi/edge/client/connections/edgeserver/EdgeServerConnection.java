package org.rifidi.edge.client.connections.edgeserver;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.jms.JMSException;

import org.rifidi.edge.client.connections.exceptions.CannotCreateRemoteReaderException;
import org.rifidi.edge.client.connections.registryservice.RemoteReaderConnectionRegistryService;
import org.rifidi.edge.client.connections.remotereader.RemoteReaderID;
import org.rifidi.edge.client.connections.util.JMSConsumerFactory;
import org.rifidi.edge.core.exceptions.RifidiException;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESServerDescription;
import org.rifidi.edge.core.rmi.client.pluginstub.RPServerDescription;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.FormAnnotationListWrapper;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.rmi.utils.cache.util.RemoteStubCacheUtil;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class provides the data structure that represents a connection to the
 * edge server to the rest of UI. It can be in one of two states: connected or
 * disconnected. Connected means that it has an active connection to the RMI
 * server. Disconnected means that an active connection to the RMI server cannot
 * be currently obtained
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerConnection extends EdgeServerConnectionState {

	/**
	 * Logger for this class
	 */
	// private Log logger = LogFactory.getLog(EdgeServerConnection.class);
	/**
	 * A description of the connection to the Edge Server RMI stub
	 */
	ESServerDescription edggeServerSD;

	/**
	 * A description of the connection to the Plugin Manager RMI stub
	 */
	RPServerDescription readerPluginManagerSD;

	/**
	 * The port number for the Edge Server's JMS service
	 */
	private int jmsPort;

	/**
	 * The local registry for storing RemoteReaders
	 */
	RemoteReaderConnectionRegistryService remoteReaderConnectionRegistryService;

	/**
	 * The JMS Consumer Factory for creating new JMS queue endpoints
	 */
	JMSConsumerFactory jmsFactory;

	/**
	 * The remote readers associated with this edge server instance
	 */
	CopyOnWriteArraySet<RemoteReaderID> readerIDs = new CopyOnWriteArraySet<RemoteReaderID>();

	/**
	 * The id for this edge server connection
	 */
	int edgeServerConnectionID;

	/**
	 * The current state of the Edge Server connection
	 */
	private EdgeServerConnectionState currentState;

	/**
	 * Behaviors in a connected state
	 */
	private EdgeServerConnectionStateConnected connectedState;

	/**
	 * Behaviors in a disconnected state
	 */
	private EdgeServerConnectionStateDisconnected disconnectedState;

	/**
	 * @param hostname
	 *            The IP of the edge
	 * @param port
	 *            The port for the edge server's RMI service
	 * @param jmsPort
	 *            The port for the edge server's JMS service
	 * @param edgeServerConnectionID
	 *            The ID associated with this edge server connection
	 * @throws RemoteException
	 * @throws NotBoundException
	 * @throws JMSException
	 */
	public EdgeServerConnection(String hostname, int port, int jmsPort,
			int edgeServerConnectionID) throws RemoteException,
			NotBoundException, JMSException {
		connectedState = new EdgeServerConnectionStateConnected(this);
		disconnectedState = new EdgeServerConnectionStateDisconnected(this);
		this.currentState = connectedState;
		ServiceRegistry.getInstance().service(this);
		this.jmsPort = jmsPort;
		this.edgeServerConnectionID = edgeServerConnectionID;

		this.edggeServerSD = new ESServerDescription(hostname, port);
		this.readerPluginManagerSD = new RPServerDescription(hostname, port);

		this.jmsFactory = new JMSConsumerFactory("tcp://" + hostname + ":"
				+ this.jmsPort);
	}

	@Override
	public boolean isConnected() {
		return currentState.isConnected();
	}

	@Override
	public RemoteReaderID createRemoteReader(ReaderInfoWrapper wrapper)
			throws CannotCreateRemoteReaderException {
		return currentState.createRemoteReader(wrapper);
	}

	@Override
	public void destroyRemoteReader(RemoteReaderID ID) {
		currentState.destroyRemoteReader(ID);
	}

	@Override
	public Set<RemoteReaderID> getRemoteReaderIDs() {
		return currentState.getRemoteReaderIDs();
	}

	@Override
	public List<String> getAvailableReaderPlugins() {
		return currentState.getAvailableReaderPlugins();
	}

	@Override
	public String getReaderInfoAnnotation(String readerInfoClassName)
			throws RifidiReaderInfoNotFoundException {
		return currentState.getReaderInfoAnnotation(readerInfoClassName);
	}

	@Override
	public ReaderPluginWrapper getReaderPluginWrapper(String readerInfoClassName)
			throws RifidiReaderPluginXMLNotFoundException, ServerUnavailable {
		return currentState.getReaderPluginWrapper(readerInfoClassName);
	}

	@Override
	public FormAnnotationListWrapper getReaderPropertyAnnotations(
			String readerInfoClassName) throws ServerUnavailable,
			RifidiException {
		return this.currentState
				.getReaderPropertyAnnotations(readerInfoClassName);
	}

	@Override
	public FormAnnotationListWrapper getReaderCommandAnnotations(
			String readerInfoClassName) throws ServerUnavailable,
			RifidiException {
		return this.currentState
				.getReaderCommandAnnotations(readerInfoClassName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.connections.edgeserver.EdgeServerConnectionState
	 * #resetConnection()
	 */
	@Override
	public void resetConnection() {
		this.currentState.resetConnection();

	}

	/**
	 * Returns the ID for this edge server
	 * 
	 * @return
	 */
	public int getID() {
		return this.edgeServerConnectionID;
	}

	/**
	 * This method cleans up the EdgeServerConnection object. It should be
	 * called when the edge server is deleted.
	 */
	public void destroy() {
		// TODO: this totally breaks the design of a transparent cache
		RemoteStubCacheUtil.remove(this.edggeServerSD);
		RemoteStubCacheUtil.remove(this.readerPluginManagerSD);
		try {
			jmsFactory.stop();
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "Server " + this.edgeServerConnectionID;
	}

	/**
	 * Move to the Connected state
	 */
	protected void transitionToConnected() {
		this.currentState = connectedState;
	}

	/**
	 * Move to the disconnected state
	 */
	protected void transitionToDisconnected() {
		// TODO: display a dialog box
		this.currentState = disconnectedState;
	}

	@Inject
	public void setRemoteReaderRegistryService(
			RemoteReaderConnectionRegistryService readerConnectionRegistryService) {
		this.remoteReaderConnectionRegistryService = readerConnectionRegistryService;
	}
}
