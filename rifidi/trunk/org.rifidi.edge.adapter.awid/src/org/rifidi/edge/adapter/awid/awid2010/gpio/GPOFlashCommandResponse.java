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

import org.rifidi.edge.adapter.awid.awid2010.communication.messages.AbstractAwidMessage;

/**
 * This class represents a response from the AWID reader to the 'Flash On/Off
 * Control' command
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class GPOFlashCommandResponse extends AbstractAwidMessage {

	/**
	 * Constructor
	 * 
	 * @param rawmessage
	 */
	public GPOFlashCommandResponse(byte[] rawmessage) {
		super(rawmessage);
	}

	/**
	 * 
	 * @return true if the flash message succeeded.
	 */
	public boolean flashSuceeded() {
		return this.rawmessage[3] == 0x00;
	}

	/**
	 * 
	 * @return Return true if teh flash failed because the pin was busy
	 */
	public boolean pinBusy() {
		return this.rawmessage[3] == 0x80;
	}

}
