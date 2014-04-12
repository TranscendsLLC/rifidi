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
 * This class represents the bytes to send to the AWID the 'Flash On/Off
 * Control' command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOFlashCommand extends AbstractAwidCommand {

	/**
	 * Constructor
	 * 
	 * @param pin
	 *            The pin to set
	 * @param onTime
	 *            The time to hold the line high in 100 ms
	 * @param offTime
	 *            The time to hold the line low in 100 ms
	 * @param totalCount
	 *            The number of times to flash
	 */
	public GPOFlashCommand(byte pin, byte onTime, byte offTime, byte totalCount) {
		super();
		if (pin < 0 || pin > 3) {
			throw new IllegalArgumentException("Pin must be 0>=pin>=3");
		}
		this.rawmessage = new byte[] { 0x09, 0x00, 0x02, pin, onTime, offTime,
				totalCount };
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AWID Flash On/Off Control: " + getCommandAsString();
	}

}
