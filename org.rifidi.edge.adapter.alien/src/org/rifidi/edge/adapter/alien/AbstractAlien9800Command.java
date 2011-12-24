/*
 * 
 * AbstractAlien9800Command.java
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
package org.rifidi.edge.adapter.alien;

import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * A superclass for use by commands for the Alien9800 Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractAlien9800Command extends TimeoutCommand {

	/**
	 * Default Constructor
	 * 
	 * @param commandID
	 *            The FACTORY_ID of the command.
	 */
	public AbstractAlien9800Command(String commandID) {
		super(commandID);
	}
}
