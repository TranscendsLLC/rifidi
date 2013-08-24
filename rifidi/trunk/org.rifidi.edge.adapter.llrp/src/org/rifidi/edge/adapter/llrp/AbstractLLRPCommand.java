/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp;

import org.rifidi.edge.sensors.TimeoutCommand;

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
