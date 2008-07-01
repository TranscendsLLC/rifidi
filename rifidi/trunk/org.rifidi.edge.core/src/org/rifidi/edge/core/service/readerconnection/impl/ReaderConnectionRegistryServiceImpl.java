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
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.impl.ReaderConnection;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.factory.ReaderPluginFactory;
import org.rifidi.edge.core.readerPlugin.registry.ReaderPluginRegistryChangeListener;
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

	private HashMap<Integer, ReaderConnection> readerConnectionRegistry;

	private int counter;

	// private ConnectionFactory connectionFactory;

	private ReaderPluginFactory readerPluginFactory;

	/**
	 * The constructor for this object
	 */
	public ReaderConnectionRegistryServiceImpl() {
		ServiceRegistry.getInstance().service(this);
		readerPluginFactory = new ReaderPluginFactory();
		readerConnectionRegistry = new HashMap<Integer, ReaderConnection>();
		initialize();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#initialize()
	 */
	@Override
	public void initialize() {
		// initialize counter if ReaderAdapter where loaded
		counter = 0;
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

		if (counter >= Integer.MAX_VALUE - 100)
			throw new RuntimeException("Session counter reached max value: "
					+ (Integer.MAX_VALUE - 100));

		logger.debug("Connecting to Reader "
				+ abstractConnectionInfo.getClass().getName() + "://"
				+ abstractConnectionInfo.getIPAddress() + ":"
				+ abstractConnectionInfo.getPort());

		// Unique identifier for ReaderConnections (also used as name for the
		// JMS Queue)
		counter++;

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
						.getProtocol(), counter);

		// register readerConnection in registry
		readerConnectionRegistry.put(counter, connection);

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
			AbstractReaderInfo abstractConnectionInfo = getReaderConnection(
					readerConnectionID).getConnectionInfo();
			logger.debug("Deleting to Reader "
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
}
