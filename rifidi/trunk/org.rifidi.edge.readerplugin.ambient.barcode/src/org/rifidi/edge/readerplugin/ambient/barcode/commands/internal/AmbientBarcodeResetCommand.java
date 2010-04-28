/*
 *  AmbientBarcodeResetCommand.java
 *
 *  Created:	Apr 23, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.ambient.barcode.commands.internal;

import org.rifidi.edge.core.sensors.commands.Command;

/**
 * Reset command for this reader. Doesn't do anything, this reader has no
 * commands and does not need to be reset.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AmbientBarcodeResetCommand extends Command {

	/**
	 * Constructor required by superclass.  
	 * 
	 * @param commandID
	 */
	public AmbientBarcodeResetCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// Do nothing, no commands.
	}

}
