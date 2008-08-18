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
package org.rifidi.edge.core.rmi.readerconnection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;

/**
 * This is the RemoteReaderConnectionRegistry. It's used for storing
 * RemoteReaderConnections and for creating and deleting them. It also provides
 * information about current ReaderConnections and available ReaderPlugins.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface RemoteReaderConnectionRegistry extends Remote {

	public RemoteReaderConnection createReaderConnection(String connectionInfo)
			throws RemoteException, RifidiReaderInfoNotFoundException;

	public String getReaderInfoAnnotation(String readerInfoClassName)
	throws RemoteException, RifidiReaderInfoNotFoundException,
	TransformerException;

	/**
	 * Delete a previous made ReaderConnection
	 * 
	 * @param remoteReaderConnection
	 *            the RemoteReaderConnection to delete
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public void deleteReaderConnection(
			RemoteReaderConnection remoteReaderConnection)
			throws RemoteException;

	/**
	 * Get all available ReaderConnections
	 * 
	 * @return a List of RemoteReaderConnections
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public List<RemoteReaderConnection> getAllReaderConnections()
			throws RemoteException;

	/**
	 * Show the names of the registered ReaderPlugin ReaderInfos
	 * 
	 * @return List of Names of the known ReaderPlugin ReaderInfos
	 * @throws RemoteException
	 *             if an error occurs
	 */
	public List<String> getAvailableReaderPlugins() throws RemoteException;
	
	public String getReaderPluginXML(String readerInfoClassName) throws RemoteException, RifidiReaderPluginXMLNotFoundException;

}
