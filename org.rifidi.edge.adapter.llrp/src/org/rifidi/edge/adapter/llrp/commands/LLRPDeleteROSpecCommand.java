/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
