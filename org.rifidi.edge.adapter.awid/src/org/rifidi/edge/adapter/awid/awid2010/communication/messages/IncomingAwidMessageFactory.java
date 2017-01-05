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

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a factory that parses a raw awid byte message into a message object
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class IncomingAwidMessageFactory {

	/** The readerID this factory is associated with */
	private final String readerID;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(IncomingAwidMessageFactory.class);

	/**
	 * @param readerID
	 */
	public IncomingAwidMessageFactory(String readerID) {
		super();
		this.readerID = readerID;
	}

	/**
	 * This method turns an awid message from a byte[] to an object
	 * 
	 * @param message
	 *            A byte[] that is an awid message
	 * @return An object that subclasses AbstractAwidMessage
	 * @throws InvalidAwidMessageException
	 *             If the byte[] is invalid
	 */
	public AbstractAwidMessage getMessage(byte[] message)
			throws InvalidAwidMessageException {

		//logger.debug("Raw data " + new String(Hex.encodeHex(message)));

		if (message[1] == 0x20
				&& (message[2] == 0x1E || message[2] == 0x5E)) {
			return new Gen2PortalIDResponse(message, readerID, true);
		} else if (message[1] == 0x20 && message[2] == 0x0D) {
			// TODO: this logic does not work. We cannot determine the memory
			// bank based on the length of the message
			if (message[0] == 0x13) {
				return new Gen2ReadBlockDataResponse(message, 0, readerID);
			} else if (message[0] == 0x1B) {
				return new Gen2ReadBlockDataResponse(message, 1, readerID);
			} else if (message[0] == 0x0F) {
				return new Gen2ReadBlockDataResponse(message, 2, readerID);
			} else if (message[0] == 0x0C) {
				return new Gen2ReadBlockDataResponse(message, 3, readerID);
			}
			// 0x06 0xFF 0xFF 0x00 0x6F 0xC5
		} else if (message[0] == 0x06 && message[1] == 0xFF
				&& message[2] == 0xFF && message[3] == 0x00
				&& message[4] == 0x6F && message[5] == 0xC5) {
			// TODO success on output command execution.
			logger.debug("success on output command execution.");

		} else if (message[0] == 0x06 && message[1] == 0xFF
				&& message[2] == 0xFF && message[3] == 0xFF
				&& message[4] == 0x71 && message[5] == 0x35) {
			// TODO Output command execution failed.
			logger.warn("Output command execution failed.");
		} else if (message[1] == (byte) 0xFF && message[2] == 0x1E) {
			logger.debug("Other " + message[2]);
			return new PortalIDError(message);
		} else if (message.length > 3) {
			String s = new String(Hex.encodeHex(message)).toUpperCase();
			logger.warn("Unknown message: " + s);
		}
		throw new InvalidAwidMessageException("Unknown Message: "
				+ new String(Hex.encodeHex(message)));

	}
}
