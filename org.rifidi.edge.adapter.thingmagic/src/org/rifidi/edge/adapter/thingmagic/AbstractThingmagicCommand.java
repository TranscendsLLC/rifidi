/*
 *  AbstractThingmagicCommand.java
 *
 *  Created:	Sep 29, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.thingmagic;

import org.rifidi.edge.sensors.Command;

/**
 * A superclass for use by commands for the Thingmagic Reader
 * 
 * @author Matthew Dean
 */
public abstract class AbstractThingmagicCommand extends Command {

	/**
	 * Default constructor.  
	 * 
	 * @param commandID
	 */
	public AbstractThingmagicCommand(String commandID) {
		super(commandID);
	}
}
