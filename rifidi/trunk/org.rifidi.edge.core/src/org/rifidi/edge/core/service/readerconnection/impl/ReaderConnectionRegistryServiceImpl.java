package org.rifidi.edge.core.service.readerconnection.impl;

/*
 *  ReaderConnectionRegistryServiceImpl.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.impl.ReaderConnection;
import org.rifidi.edge.core.connection.listener.ReaderConnectionListener;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.factory.ReaderPluginFactory;
import org.rifidi.edge.core.readerPlugin.listener.ReaderPluginRegistryChangeListener;
import org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public class ReaderConnectionRegistryServiceImpl implements
		ReaderConnectionRegistryService, ReaderPluginRegistryChangeListener {
	private Log logger = LogFactory
			.getLog(ReaderConnectionRegistryServiceImpl.class);

	private Map<Integer, ReaderConnection> readerConnectionRegistry;

	//TODO: Look if this is needed latter.
	Set<ReaderConnectionListener> listeners = Collections.synchronizedSet(new HashSet<ReaderConnectionListener>());
	
	//Random random;
	
	int connectionID;

	// private ConnectionFactory connectionFactory;

	private ReaderPluginFactory readerPluginFactory;

	/**
	 * The constructor for this object
	 */
	public ReaderConnectionRegistryServiceImpl() {
		ServiceRegistry.getInstance().service(this);
		readerPluginFactory = new ReaderPluginFactory();
		
		//TODO: Look if this is needed latter.
		readerConnectionRegistry = Collections.synchronizedMap(new HashMap<Integer, ReaderConnection>());
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#initialize()
	 */
	@Override
	public void initialize() {
		// initialize random number generator 
		//random = new Random();
		
		connectionID = 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#createReaderConnection(org.rifidi.edge.core.readerPlugin.AbstractReaderInfo)
	 */
	@Override
	public IReaderConnection createReaderConnection(
			AbstractReaderInfo abstractConnectionInfo) {
		
		
		if (abstractConnectionInfo == null)
			throw new IllegalArgumentException("Null references not allowed.");
		
//		// Worst case thinking
//		if (readerConnectionRegistry.size() > Integer.MAX_VALUE - 100){
//			throw new IllegalStateException("The max number of reader adapters is " + (Integer.MAX_VALUE - 100));
//		}
//		
//		int connectionID = random.nextInt(Integer.MAX_VALUE);
//		
//		for (int counter = 0; readerConnectionRegistry.containsKey(connectionID); counter++){
//			connectionID = random.nextInt(Integer.MAX_VALUE);
//			
//			/* Reset the random number generator after 50 tries.
//			 * Basically getting a new seed that should be fairly
//			 * unique from any other instantiation of Random.
//			 * See java documentation for Random()
//			 */
//			if (counter < 50) {
//				random.setSeed(random.nextLong() + System.nanoTime());
//				counter = 0;
//			}
//		}
		
//		if (readerConnectionRegistry.size() >= Integer.MAX_VALUE - 200)
//			throw new IllegalStateException("Session counter reached max value: "
//					+ (Integer.MAX_VALUE - 200));
		
		if (connectionID > Integer.MAX_VALUE -10)
			connectionID = 0;

		connectionID++;
		
		logger.debug("Connecting to Reader "
				+ abstractConnectionInfo.getClass().getName() + "://"
				+ abstractConnectionInfo.getIPAddress() + ":"
				+ abstractConnectionInfo.getPort());


		// TODO check for null
		// Build ReaderPlugin
		IReaderPlugin readerPlugin = readerPluginFactory
				.createReaderAdapter(abstractConnectionInfo);

		// Build JMS Queue and JMS Thread
		// JMSHelper jmsHelper = new JMSHelper();
		// jmsHelper.initializeJMSQueue(connectionFactory, Integer
		// .toString(counter));

		// Build Connection Object for holding JMS and Plugin
		ReaderConnection connection = new ReaderConnection(
				abstractConnectionInfo, readerPlugin, abstractConnectionInfo
						.getProtocol(), connectionID);

		// register readerConnection in registry
		readerConnectionRegistry.put(connectionID, connection);

		// fire events.
		for (ReaderConnectionListener listener : listeners){
			listener.readerConnectionRegistryAddEvent(connection.getConnectionInfo());
		}
		return connection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#getReaderConnection(int)
	 */
	@Override
	public IReaderConnection getReaderConnection(int readerConnectionID) {
		return readerConnectionRegistry.get(readerConnectionID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#deleteReaderConnection(int)
	 */
	@Override
	public void deleteReaderConnection(int readerConnectionID) {
		ReaderConnection readerConnection = readerConnectionRegistry
				.remove(readerConnectionID);
		if (readerConnection != null) {
			//fire events
			for (ReaderConnectionListener listener :listeners){
				listener.readerConnectionRegistryRemoveEvent(readerConnection.getConnectionInfo());
			}
			AbstractReaderInfo abstractConnectionInfo = getReaderConnection(
					readerConnectionID).getConnectionInfo();
			logger.debug("Deleting Reader "
					+ abstractConnectionInfo.getClass().getName() + "://"
					+ abstractConnectionInfo.getIPAddress() + ":"
					+ abstractConnectionInfo.getPort());

			readerConnection.cleanUp();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#deleteReaderConnection(org.rifidi.edge.core.connection.IReaderConnection)
	 */
	@Override
	public void deleteReaderConnection(IReaderConnection readerConnection) {
		if (!readerConnectionRegistry.containsKey(readerConnection
				.getSessionID()))
			return;
		AbstractReaderInfo abstractConnectionInfo = readerConnection
				.getConnectionInfo();
		logger.debug("Deleting to Reader "
				+ abstractConnectionInfo.getClass().getName() + "://"
				+ abstractConnectionInfo.getIPAddress() + ":"
				+ abstractConnectionInfo.getPort());
		readerConnectionRegistry.remove(readerConnection.getSessionID());
		((ReaderConnection) readerConnection).cleanUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#getAllReaderConnections()
	 */
	@Override
	public List<ReaderConnection> getAllReaderConnections() {
		logger.debug("Getting all active connections.");
		return new ArrayList<ReaderConnection>(readerConnectionRegistry
				.values());
	}

	// /**
	// * A method helper for the services injection framework.
	// *
	// * @param connectionFactory
	// */
	// @Inject
	// public void setConnectionFactory(ConnectionFactory con// for
	// (ReaderConnection c : connectionRegistryService
	// .getAllReaderConnections()) {
	// System.out.println(c.getAdapter());
	// }nectionFactory) {
	// this.connectionFactory = connectionFactory;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryChangeListener#readerPluginRegistryAddEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginRegistryAddEvent(
			Class<? extends AbstractReaderInfo> info) {
		//TODO: Implement listening event.  
		// We ignore this event because there is no need for it, yet. 

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryChangeListener#readerPluginRegistryRemoveEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginRegistryRemoveEvent(
			Class<? extends AbstractReaderInfo> info) {
		//Connections to delete
		Collection<ReaderConnection> connections = new ArrayList<ReaderConnection>();

		// look up all connections that use that reader info and delete them.
		
		//KLUDGE: If we don't do it this way we get a ConcurrentModificationException!
		/* first we look up the connections that need to be removed and store them
		 * in a Collection
		 */
		for (ReaderConnection connection : 
				readerConnectionRegistry.values()) {
			if (connection.getConnectionInfo().getClass().equals(info)) {
				connections.add(connection);
			}
		}
		
		/* then we remove all connections in that Collection
		 */
		for (ReaderConnection connection: connections ){
			deleteReaderConnection(connection);
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService#addEventListener(org.rifidi.edge.core.connection.listener.ReaderConnectionListener)
	 */
	@Override
	public void addEventListener(ReaderConnectionListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService#removeEventListener(org.rifidi.edge.core.connection.listener.ReaderConnectionListener)
	 */
	@Override
	public void removeEventListener(ReaderConnectionListener listener) {
		listeners.remove(listener);
	}
}
