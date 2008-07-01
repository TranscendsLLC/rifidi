/*
 *  ReaderConnectionRegistryService.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.service.readerconnection;

import java.util.List;

import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.impl.ReaderConnection;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public interface ReaderConnectionRegistryService {

	/**
	 * Initializes the Registry Service
	 */
	public void initialize();

	/**
	 * Creates a reader connection.
	 * @param abstractConnectionInfo The connection info for a reader
	 * @return A new connection to the reader
	 */
	public IReaderConnection createReaderConnection(AbstractReaderInfo abstractConnectionInfo);

	/**
	 * Returns the reader connection based 
	 * @param readerConnectionID 
	 * @return
	 */
	public IReaderConnection getReaderConnection(int readerConnectionID);
	
	/**
	 * Deletes a connection based on its ID
	 * @param readerConnectionID The ID of the connection to delete
	 */
	public void deleteReaderConnection(int readerConnectionID);
	
	/**
	 * Delete a connection based on the connection object itself.
	 * @param readerConnection The connection object
	 */
	public void deleteReaderConnection(IReaderConnection readerConnection);

	/**
	 * Returns the list of all active connections in this registry
	 * @return The list of active connections
	 */
	public List<ReaderConnection> getAllReaderConnections();
	
	//TODO Jerry add and remove Listener

}