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
 * This is a response message for when the second byte is 0xFF and the thrid
 * byte is 0x1E. The message is sent back from the awid reader when a portal ID
 * command times out
 * 
 * @author Kyle Neumeier - kyle@pramari.
 * 
 */
public class PortalIDError extends AbstractAwidMessage {

	public PortalIDError(byte[] rawmessage) {
		super(rawmessage);
	}

}
