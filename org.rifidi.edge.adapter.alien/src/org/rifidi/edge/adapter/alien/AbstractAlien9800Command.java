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
/**
 * 
 */
package org.rifidi.edge.adapter.alien;

import org.rifidi.edge.sensors.TimeoutCommand;

/**
 * A superclass for use by commands for the Alien9800 Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractAlien9800Command extends TimeoutCommand {

	/**
	 * Default Constructor
	 * 
	 * @param commandID
	 *            The FACTORY_ID of the command.
	 */
	public AbstractAlien9800Command(String commandID) {
		super(commandID);
	}
}
