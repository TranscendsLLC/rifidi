/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api;

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
