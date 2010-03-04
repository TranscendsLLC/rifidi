/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.gpio;

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;

/**
 * This class is the parsing strategy for incoming GPI/O messages for the AWID
 * reader.
 * 
 * It reads the first byte (which is the length byte), then reads that number of
 * bytes.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidGPIOMessageParsingStrategy implements MessageParsingStrategy {

	/** The message */
	private byte[] messageBytes;
	/** The number of bytes read so far */
	private byte byteCount = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy#isMessage
	 * (byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		// increment the counter
		byteCount++;
		// if this is the first byte
		if (byteCount == 1) {
			// if the first byte is a 0x00 or a 0xFF, it's an ACK, so just
			// return the ACK
			if (message == 0x00 || message == (byte) 0xFF) {
				return new byte[] { message };
			}
			// create the array
			messageBytes = new byte[message];
		}
		// add the byte to the array
		messageBytes[byteCount - 1] = message;

		// if we have received the total number of bytes, return it.
		if (byteCount == messageBytes[0]) {
			return messageBytes;
		}

		// return null if we havent' received all the bytes yet
		return null;

	}

}
