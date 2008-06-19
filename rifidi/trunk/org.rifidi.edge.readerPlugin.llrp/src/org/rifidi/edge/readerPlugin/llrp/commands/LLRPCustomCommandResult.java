/*
 *  LLRPCustomCommandResult.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.llrp.commands;

import org.llrp.ltk.types.LLRPMessage;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommandResult;


/**
 * The result for a custom command for an LLRP reader.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPCustomCommandResult implements ICustomCommandResult {

	/**
	 * The generated UID.  
	 */
	private static final long serialVersionUID = -291087004312152570L;
	
	/**
	 * The LLRPMessage result of a custom command.  
	 */
	private LLRPMessage result;


	/**
	 * Get the result 
	 * 
	 * @return the result
	 */
	public LLRPMessage getResult() {
		return result;
	}


	/**
	 * Set the message result for the LLRP reader.  
	 * 
	 * @param result the result to set
	 */
	public void setResult(LLRPMessage result) {
		this.result = result;
	}

}
