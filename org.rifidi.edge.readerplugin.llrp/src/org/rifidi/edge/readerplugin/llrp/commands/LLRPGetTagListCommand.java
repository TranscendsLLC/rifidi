/*
 *  LLRPGetTagListCommand.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;

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
	
	private int roSpecID = 0;
	
	/**
	 * Constructor for LLRPGetTagListCommand
	 * 
	 * @param commandID	The ID for this command.  
	 */
	public LLRPGetTagListCommand(String commandID) {
		super(commandID);
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.session = (LLRPReaderSession)this.readerSession;
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
