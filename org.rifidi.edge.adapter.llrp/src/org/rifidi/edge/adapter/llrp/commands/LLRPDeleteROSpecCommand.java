/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp.commands;

import java.util.concurrent.TimeoutException;

import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.types.UnsignedInteger;
import org.rifidi.edge.adapter.llrp.AbstractLLRPCommand;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;

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
	 * @see org.rifidi.edge.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		this.session = (LLRPReaderSession) this.sensorSession;

		DELETE_ROSPEC delROSpec = new DELETE_ROSPEC();
		delROSpec.setROSpecID(new UnsignedInteger(rospec_id));

		// TODO: error check?
		session.transact(delROSpec);

	}

	/**
	 * @param id
	 */
	public void setROSPecID(int id) {
		this.rospec_id = id;
	}
}
