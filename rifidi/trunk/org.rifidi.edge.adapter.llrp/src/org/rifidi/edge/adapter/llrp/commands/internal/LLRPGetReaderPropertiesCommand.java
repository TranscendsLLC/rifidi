/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
