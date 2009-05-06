/*
 *  AbstractLLRPCommand.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp;
//TODO: Comments
import org.rifidi.edge.core.commands.Command;

/**
 * 
 * 
 * @author Matthew Dean
 */
public abstract class AbstractLLRPCommand extends Command {

	/**
	 * 
	 * @param commandID
	 */
	public AbstractLLRPCommand(String commandID) {
		super(commandID);
	}
}
