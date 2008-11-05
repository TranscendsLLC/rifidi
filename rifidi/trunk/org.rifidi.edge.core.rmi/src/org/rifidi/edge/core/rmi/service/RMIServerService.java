/*
 *  RMIServerService.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.rmi.service;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * This interface describes the Methods the RMIServer should provide
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface RMIServerService {

	/**
	 * Start the RMIServer
	 */
	public void start();

	/**
	 * Stop the RMIServer
	 */
	public void stop();

	/**
	 * Binds the given Object to RMI
	 * 
	 * @param o
	 *            the Object to bind
	 * @throws RemoteException
	 */
	public void bindToRMI(UnicastRemoteObject obj, String id)
			throws RemoteException;

	/**
	 * Unbinds the object from RMI registry.
	 * 
	 * @param id
	 * @throws RemoteException
	 */
	public void unbindFromRMI(String id) throws RemoteException;

	/**
	 * Gets a object with the given RMI registry ID
	 * 
	 * @param id
	 * @return
	 */
	public Remote lookup(String id);

}
