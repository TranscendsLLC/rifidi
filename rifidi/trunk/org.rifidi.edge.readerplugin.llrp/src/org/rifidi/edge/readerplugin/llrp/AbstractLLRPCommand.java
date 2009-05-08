/*
 *  AbstractLLRPCommand.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp;

import org.rifidi.edge.core.commands.Command;

/**
 * This class represents an abstract command for an LLRP reader. Extend it to
 * create an LLRP command.
 * 
 * @author Matthew Dean
 */
public abstract class AbstractLLRPCommand extends Command {

	/**
	 * Constructor for the AbstractLLRPCommand.  
	 * 
	 * @param commandID	
	 */
	public AbstractLLRPCommand(String commandID) {
		super(commandID);
	}
}
