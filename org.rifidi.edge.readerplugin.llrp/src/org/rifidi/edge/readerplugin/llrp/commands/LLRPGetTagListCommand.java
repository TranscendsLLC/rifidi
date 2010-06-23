/*
 *  LLRPGetTagListCommand.java
 *
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.readerplugin.llrp.commands;

import java.util.concurrent.TimeoutException;

import org.llrp.ltk.generated.messages.GET_REPORT;
import org.rifidi.edge.readerplugin.llrp.AbstractLLRPCommand;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderSession;

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
	 * @see org.rifidi.edge.core.sensors.commands.TimeoutCommand#execute()
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
