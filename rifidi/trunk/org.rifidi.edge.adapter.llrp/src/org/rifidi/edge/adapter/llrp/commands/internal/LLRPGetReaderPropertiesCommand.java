/*
 *  LLRPGetReaderPropertiesCommand.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.llrp.commands.internal;

import java.util.concurrent.TimeoutException;

import org.rifidi.edge.adapter.llrp.AbstractLLRPCommand;

/**
 * This class gets and sets the reader properties for an LLRP reader.  
 * 
 * @author Matthew Dean
 */
public class LLRPGetReaderPropertiesCommand extends AbstractLLRPCommand {

	/**
	 * Constructor for LLRPGetReaderPropertiesCommand
	 * 
	 * @param commandID
	 */
	public LLRPGetReaderPropertiesCommand(String commandID) {
		super(commandID);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		// TODO Auto-generated method stub
		
	}
	
}
