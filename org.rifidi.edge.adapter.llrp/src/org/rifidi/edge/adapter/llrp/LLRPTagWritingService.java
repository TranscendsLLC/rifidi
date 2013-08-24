/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp;

import java.util.concurrent.TimeUnit;

import org.rifidi.edge.sensors.AbstractTagWritingService;
import org.rifidi.edge.sensors.CannotExecuteException;

/**
 * @author kyle
 *
 */
public class LLRPTagWritingService extends AbstractTagWritingService<LLRPReaderSession> {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.management.AbstractTagWritingService#writeEPC(java.lang.String, int, byte[])
	 */
	@Override
	public void writeEPC(String readerID, int antenna, byte[] data)
			throws CannotExecuteException {
		WriteCommand command = new WriteCommand("Write Command ID");
		super.getSession(readerID).submitAndBlock(command, 5000, TimeUnit.MILLISECONDS);
		if(command.getException()!=null){
			throw command.getException();
		}
		
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.management.AbstractTagWritingService#writeUser(java.lang.String, int, byte[])
	 */
	@Override
	public void writeUser(String readerID, int antenna, byte[] data)
			throws CannotExecuteException {
		WriteCommand command = new WriteCommand("Write Command ID");
		super.getSession(readerID).submitAndBlock(command, 5000, TimeUnit.MILLISECONDS);
		if(command.getException()!=null){
			throw command.getException();
		}
		
	}
	
}
