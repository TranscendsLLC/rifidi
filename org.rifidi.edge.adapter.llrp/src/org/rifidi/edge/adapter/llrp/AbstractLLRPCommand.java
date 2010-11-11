/*
 * 
 * AbstractLLRPCommand
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.adapter.llrp;

import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.core.sensors.commands.TimeoutCommand;

/**
 * This class represents an abstract command for an LLRP reader. Extend it to
 * create an LLRP command.
 * 
 * @author Matthew Dean
 */
public abstract class AbstractLLRPCommand extends TimeoutCommand {

	/**
	 * Constructor for the AbstractLLRPCommand.  
	 * 
	 * @param commandID	
	 */
	public AbstractLLRPCommand(String commandID) {
		super(commandID);
	}
}
