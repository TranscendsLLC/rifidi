/*
 *  RemoteReaderConnection.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.rmi.api.readerconnection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Set;

import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;

/**
 * This is the EdgeServerStub. It is used as the main interface into the edge
 * server, for example, to create and delete reader sessions
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface EdgeServerStub extends Remote {

	/**
	 * Create a ReaderSession
	 * 
	 * @param connectionInfo
	 *            The ReaderInfo for the reader session to create
	 * @return A long that is the ID of the reader session
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiReaderInfoNotFoundException
	 *             If the supplied connectionInfo is invalid
	 */
	public Long createReaderSession(String connectionInfo)
			throws RemoteException, RifidiReaderInfoNotFoundException;

	/**
	 * Delete a reader session from the edge server
	 * 
	 * @param readerSessionID
	 *            The ID of the session to delete
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public void deleteReaderSession(Long readerSessionID)
			throws RemoteException;

	/**
	 * Gets all the IDs of the Sessions that are currently on the edge server
	 * 
	 * @return A set of Longs that are the IDs of all the current sessions
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public Set<Long> getAllReaderSessions() throws RemoteException;

	/**
	 * This method returns the ReaderSessionStubs for all of the current
	 * readers. Normally remote objects should be obtained directly from the RMI
	 * registry. However in some cases (such as when there are a large number of
	 * readers currently on the reader), it makes sense for performance reasons
	 * to get all the sessions at once
	 * 
	 * @param readerSessionIds
	 *            The IDs of the sessions to get
	 * @return A hashmap of IDs to Remote Session stubs
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public HashMap<Long, ReaderSessionStub> getReaderSessionStubs(
			Set<Long> readerSessionIds) throws RemoteException;

}
