package org.rifidi.edge.client.connections.registryservice.impl;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.jms.JMSException;

import org.rifidi.edge.client.connections.edgeserver.EdgeServerConnection;
import org.rifidi.edge.client.connections.exceptions.EdgeServerConnectionRegistryException;
import org.rifidi.edge.client.connections.registryservice.EdgeServerConnectionRegistryService;
import org.rifidi.edge.client.connections.registryservice.listeners.EdgeServerConnectionRegistryListener;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * This class is the implementation for the EdgeServerConnectionRegistryService.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerConnectionRegistryServiceImpl implements
		EdgeServerConnectionRegistryService {

	private int id;
	private HashMap<Integer, EdgeServerConnection> connections;
	private CopyOnWriteArraySet<EdgeServerConnectionRegistryListener> listeners;

	public EdgeServerConnectionRegistryServiceImpl() {
		this.id = 0;
		this.connections = new HashMap<Integer, EdgeServerConnection>();
		this.listeners = new CopyOnWriteArraySet<EdgeServerConnectionRegistryListener>();
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public synchronized int createConnection(String ip, int port, int jmsPort) throws RemoteException, NotBoundException, JMSException {

			id++;
			EdgeServerConnection connection = new EdgeServerConnection(ip,
					port, jmsPort, id);
			connections.put(id, connection);

			for (EdgeServerConnectionRegistryListener l : listeners) {
				l.serverAdded(connection);
			}

			return id;

	}

	@Override
	public synchronized void destroyConnection(int id) {
		EdgeServerConnection connection = connections.remove(id);
		if (connection != null) {
			connection.destroy();
			for (EdgeServerConnectionRegistryListener l : listeners) {
				l.serverRemoved(connection);
			}
		}

	}

	@Override
	public synchronized EdgeServerConnection getConnection(int id) {
		return connections.get(id);
	}

	@Override
	public Set<Integer> getConnectionIDs() {
		Set<Integer> retVal = new HashSet<Integer>();
		synchronized (this) {
			retVal = new HashSet<Integer>(connections.keySet());
		}
		return retVal;
	}

	/**
	 * A convience method that is not part of the interface
	 * 
	 * @return
	 */
	public EdgeServerConnection[] getConnections() {
		Set<Integer> serverIDs = this.getConnectionIDs();
		EdgeServerConnection[] connections = new EdgeServerConnection[serverIDs
				.size()];
		int i = 0;
		for (Integer id : serverIDs) {
			connections[i] = this.getConnection(id);
			i++;
		}
		return connections;
	}

	@Override
	public void addListener(EdgeServerConnectionRegistryListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(EdgeServerConnectionRegistryListener listener) {
		this.listeners.remove(listener);
	}
}
