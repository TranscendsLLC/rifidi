/*
 *  AbstractAlien9800Command.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.alien;

import org.rifidi.edge.core.commands.Command;

/**
 * This class represents an abstract command for an Alien reader.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public abstract class AbstractAlien9800Command extends Command {

	/**
	 * Constructor for AbstractAlien9800Command.  
	 * 
	 * @param commandID	The ID for this command.  
	 */
	public AbstractAlien9800Command(String commandID) {
		super(commandID);
	}
}
