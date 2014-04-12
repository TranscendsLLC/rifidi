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
package org.rifidi.edge.adapter.awid.awid2010.gpio;

import org.rifidi.edge.adapter.awid.awid2010.communication.commands.AbstractAwidCommand;

/**
 * This class represents the bytes to send to the AWID the 'Output' command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOOutputCommand extends AbstractAwidCommand {

	/**
	 * Constructor
	 * 
	 * @param port
	 *            The port to set
	 * @param high
	 *            if true, set the port high. If false, set the port low
	 */
	public GPOOutputCommand(int port, boolean high) {
		super();
		if (port < 0 || port > 3) {
			throw new IllegalArgumentException("Port must be 0>=port>=3");
		}
		byte on;
		if (high) {
			on = 0x00;
		} else {
			on = 0x01;
		}
		this.rawmessage = new byte[] { 0x06, 0x00, on, (byte) port };

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AWID Output Command " + getCommandAsString();
	}

}
