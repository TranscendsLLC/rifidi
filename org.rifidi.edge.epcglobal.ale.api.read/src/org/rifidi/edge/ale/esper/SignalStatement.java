/*
 * 
 * SignalStatement.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.esper;

/**
 * A signal statement can inform listeners about certain events arriving.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface SignalStatement extends StatementController {
	/**
	 * Register a new listener.
	 * 
	 * @param listener
	 */
	public void registerSignalListener(SignalListener listener);

	/**
	 * Unregister a listener.
	 * 
	 * @param listener
	 */
	public void unregisterSignalListener(SignalListener listener);
}
