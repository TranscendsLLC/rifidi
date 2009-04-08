/*
 *  LLRPGetTagListCommand.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;

import org.llrp.ltk.generated.messages.GET_REPORT;
import org.rifidi.edge.readerplugin.llrp.AbstractLLRPCommand;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderSession;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class LLRPGetTagListCommand extends AbstractLLRPCommand {

	LLRPReaderSession session = null;
	
	private int roSpecID = 0;
	
	/**
	 * @param commandID
	 */
	public LLRPGetTagListCommand(String commandID) {
		super(commandID);
		//this.session = (LLRPReaderSession)this.readerSession;
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
	 * @param roSpecID the roSpecID to set
	 */
	public void setRoSpecID(int roSpecID) {
		this.roSpecID = roSpecID;
	}



	/**
	 * @return the roSpecID
	 */
	public int getRoSpecID() {
		return roSpecID;
	}

}
