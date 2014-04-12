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

import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * This class represents an abstract command for an LLRP reader. Extend it to
 * create an LLRP command.
 * 
 * @author Matthew Dean
 */
public abstract class AbstractLLRPCommand extends TimeoutCommand {

	/**
	 * Constructor for the AbstractLLRPCommand.  
	 * 
	 * @param commandID	
	 */
	public AbstractLLRPCommand(String commandID) {
		super(commandID);
	}
}
