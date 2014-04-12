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
