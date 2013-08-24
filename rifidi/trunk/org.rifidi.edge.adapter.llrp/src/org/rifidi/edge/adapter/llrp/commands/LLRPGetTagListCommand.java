/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp.commands;

import java.util.concurrent.TimeoutException;

import org.llrp.ltk.generated.messages.GET_REPORT;
import org.rifidi.edge.adapter.llrp.AbstractLLRPCommand;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;

/**
 * This command will get a tag list from an LLRP reader for a given ROSpec.  
 * 
 * @author Matthew Dean
 */
public class LLRPGetTagListCommand extends AbstractLLRPCommand {

	LLRPReaderSession session = null;
	
	private int roSpecID = 1;
	
	/**
	 * Constructor for LLRPGetTagListCommand
	 * 
	 * @param commandID	The ID for this command.  
	 */
	public LLRPGetTagListCommand(String commandID) {
		super(commandID);
	}
	

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		this.session = (LLRPReaderSession)this.sensorSession;
		session.send(new GET_REPORT());
		
	}


	/**
	 * Sets the ROSpecID of this command.  
	 * 
	 * @param roSpecID the roSpecID to set
	 */
	public void setRoSpecID(int roSpecID) {
		this.roSpecID = roSpecID;
	}



	/**
	 * Returns the ROSpecID of this Command.  
	 * 
	 * @return the roSpecID
	 */
	public int getRoSpecID() {
		return roSpecID;
	}

}
