/*
 * 
 * StatementController.java
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
 * Control the lifecycle of a statement.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface StatementController {
	/**
	 * Start the controlled statement.
	 */
	public void start();

	/**
	 * Stop the controlled statement.
	 */
	public void stop();

	/**
	 * True if the statement needs to be restarted for each event cycle.
	 * 
	 * @return
	 */
	boolean needsRestart();
}
