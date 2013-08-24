/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.llrp;

import java.util.concurrent.TimeoutException;

import org.rifidi.edge.sensors.CannotExecuteException;
import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * @author kyle
 *
 */
public class WriteCommand extends TimeoutCommand {

	private CannotExecuteException exception;
	
	/**
	 * @param commandID
	 */
	public WriteCommand(String commandID) {
		super(commandID);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		//InputStream stream = WriteCommand.class.getResourceAsStream("LLRPMessageTemplate/add_accessspec.llrp");
		//DELETE ALL ROSPECS 
		//DELETE ALL ACCESS SPECS
		//FORM ACCESS SPEC, SEND, & ENABLE
		//FORM ROSPEC, SEND, ENABLE, START

	}
	
	public CannotExecuteException getException(){
		return exception;
	}

}
