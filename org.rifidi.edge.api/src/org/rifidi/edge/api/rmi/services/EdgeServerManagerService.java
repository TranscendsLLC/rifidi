/*
 * EdgeServerManagerService.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.api.rmi.services;

import java.rmi.RemoteException;

/**
 * This is the interface for the Edge Server RMI Stub.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface EdgeServerManagerService{

	/**
	 * Saves the current configurations to a file
	 * 
	 * @throws RemoteException
	 */
	void save();

	/**
	 * Returns the last time this server was started.  
	 * 
	 * @return The timestamp of the last time this server was started
	 * @throws RemoteException
	 */
	Long getStartupTime();
}
