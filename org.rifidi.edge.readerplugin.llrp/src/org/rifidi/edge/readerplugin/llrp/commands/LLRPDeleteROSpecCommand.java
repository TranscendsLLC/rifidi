/*
 *  LLRPDeleteROSpecCommand.java
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
	private int rospec_id = 1;

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
		this.session = (LLRPReaderSession) this.sensorSession;

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
