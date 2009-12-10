/*
 *  AcuraProXResetCommand.java
 *
 *  Created:	Dec 8, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.acura.commands.internal;

import org.rifidi.edge.core.sensors.commands.Command;

/**
 * A reset command is required, so here it is. It doesn't do anything, it is
 * merely a placeholder.
 * 
 * @author Matthew Dean
 */
public class AcuraProXResetCommand extends Command {

	/**
	 * Constructor.  
	 * 
	 * @param commandID
	 */
	public AcuraProXResetCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// Do nothing, no reset required.
	}

}
