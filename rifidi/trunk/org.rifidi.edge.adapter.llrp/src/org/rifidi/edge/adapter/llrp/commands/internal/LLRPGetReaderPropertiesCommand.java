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
package org.rifidi.edge.adapter.llrp.commands.internal;

import java.util.concurrent.TimeoutException;

import org.rifidi.edge.adapter.llrp.AbstractLLRPCommand;

/**
 * This class gets and sets the reader properties for an LLRP reader.  
 * 
 * @author Matthew Dean
 */
public class LLRPGetReaderPropertiesCommand extends AbstractLLRPCommand {

	/**
	 * Constructor for LLRPGetReaderPropertiesCommand
	 * 
	 * @param commandID
	 */
	public LLRPGetReaderPropertiesCommand(String commandID) {
		super(commandID);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		// TODO Auto-generated method stub
		
	}
	
}
