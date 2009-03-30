/*
 *  LLRPCommandObject.java
 *
 *  Created:	Mar 9, 2009
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.llrp.commandobject;

import java.io.IOException;

import org.llrp.ltk.types.LLRPMessage;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderSession;

/**
 * 
 * 
 * @author Matthew Dean
 */
public abstract class LLRPCommandObject {

	/**
	 * 
	 */
	protected LLRPMessage command;
	/**
	 * 
	 */
	protected LLRPReaderSession readerSession;

	/**
	 * 
	 * 
	 * @param message
	 */
	public LLRPCommandObject(LLRPMessage message) {
		this.command = message;
	}
	
	/**
	 * Set the session on the reader
	 * 
	 * @param session
	 */
	public void setSession(LLRPReaderSession session) {
		this.readerSession = session;
	}
	
	/**
	 * Execute a get or set on the reader
	 * 
	 * @return The return value from the Call
	 * @throws IOException
	 */
	public abstract String execute() throws IOException, LLRPException;
	
	
}
