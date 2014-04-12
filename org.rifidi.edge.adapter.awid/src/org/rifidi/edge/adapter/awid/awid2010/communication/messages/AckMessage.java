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
package org.rifidi.edge.adapter.awid.awid2010.communication.messages;

/**
 * An awid message that represents an acknowledgement message from the awid
 * reader
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AckMessage extends AbstractAwidMessage {

	/**
	 * Constructor
	 * 
	 * @param rawmessage
	 *            The message.
	 */
	public AckMessage(byte[] rawmessage) {
		super(rawmessage);
		if (rawmessage[0] != (byte) 0x00 && rawmessage[0] != (byte) 0xFF) {
			throw new IllegalArgumentException(
					"AckMessage must be a 0x00 or 0xFF");
		}
	}

	/**
	 * Typically a 0x00 means the command this acknowledgement is for was successful. A 0xFF means the
	 * command failed.
	 * 
	 * @return true if the ack is 0x00.  false if it is 0xFF
	 */
	public boolean isSuccessful() {
		return this.rawmessage[0] == (byte) 0x00;
	}

}
