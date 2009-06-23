/*
 *  LLRPDeleteROSpecCommand.java
 *
 *  Created:	Jun 18, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.llrp.commands;

import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.types.UnsignedInteger;
import org.rifidi.edge.readerplugin.llrp.AbstractLLRPCommand;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderSession;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class LLRPDeleteROSpecCommand extends AbstractLLRPCommand {

	/**
	 * The ROSpecID.
	 */
	private int rospec_id = -1;

	/**
	 * The session.
	 */
	private LLRPReaderSession session = null;

	/**
	 * @param commandID
	 */
	public LLRPDeleteROSpecCommand(String commandID) {
		super(commandID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		this.session = (LLRPReaderSession) this.readerSession;

		DELETE_ROSPEC delROSpec = new DELETE_ROSPEC();
		delROSpec.setROSpecID(new UnsignedInteger(rospec_id));

		session.send(delROSpec);
	}

	/**
	 * @param id
	 */
	public void setROSPecID(int id) {
		this.rospec_id = id;
	}
}
