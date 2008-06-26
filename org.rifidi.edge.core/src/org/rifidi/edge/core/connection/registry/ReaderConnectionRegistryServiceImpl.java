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
package org.rifidi.edge.core.connection.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.jms.ConnectionFactory;

import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnection;
import org.rifidi.edge.core.connection.jms.JMSHelper;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.factory.ReaderPluginFactory;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public class ReaderConnectionRegistryServiceImpl implements
		ReaderConnectionRegistryService {

	private HashMap<Integer, ReaderConnection> readerConnectionRegistry;

	private int counter;

	private ConnectionFactory connectionFactory;

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

		// Unique identifier for ReaderConnections (also used as name for the
		// JMS Queue)
		counter++;

		// Build ReaderPlugin
		IReaderPlugin readerPlugin = readerPluginFactory
				.createReaderAdapter(abstractConnectionInfo);

		// Build JMS Queue and JMS Thread
		JMSHelper jmsHelper = new JMSHelper();
		jmsHelper.initializeJMSQueue(connectionFactory, Integer
				.toString(counter));

		// Build Connection Object for holding JMS and Plugin
		ReaderConnection connection = new ReaderConnection(
				abstractConnectionInfo, readerPlugin, null, counter);

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
		readerConnection.cleanUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#deleteReaderConnection(org.rifidi.edge.core.connection.IReaderConnection)
	 */
	@Override
	public void deleteReaderConnection(IReaderConnection readerConnection) {
		readerConnectionRegistry.remove(readerConnection);
		((ReaderConnection) readerConnection).cleanUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService#getAllReaderConnections()
	 */
	@Override
	public List<ReaderConnection> getAllReaderConnections() {
		return new ArrayList<ReaderConnection>(readerConnectionRegistry
				.values());
	}

	/**
	 * A method helper for the services injection framework.
	 * 
	 * @param connectionFactory
	 */
	@Inject
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

}
