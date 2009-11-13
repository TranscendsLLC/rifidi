/*
 * Awid2010MessageParsingStrategy.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010;

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;

/**
 * The message parsing strategy for an Awid2010. The purpose of this class is to
 * decide when a stream of bytes forms a logical Awid message. It works like
 * this: the isMessage method is called each time a byte is read from the
 * socket. If the collected bytes forms a complete message, it should reset
 * itself and return them. If they do not, it should return null.
 * 
 * It follows this algorithm: Read the first byte. If it is a x00 or xFF, the
 * the incoming message is an acknowledgement for a previously sent command. If
 * not, read the second byte. If both the first byte and the second byte are a
 * 'i', then the message is a welcome message, and we continue reading until we
 * see the string "MODULE", which is the end of the message. Otherwise, the
 * first byte is the number of bytes in the message, and we need to continue
 * reading until we see that number of bytes
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Awid2010MessageParsingStrategy implements MessageParsingStrategy {

	/** All the bytes seen so far. */
	private byte[] bytes;
	/** The first byte which normally represents the lengthByte */
	private byte lengthByte = -1;
	/** The second byte which normally represents the typeByte */
	private byte typeByte = -1;
	/** bytes seen so far */
	private int count = 0;
	/** A boolean that is set to true if the first two bytes are 'i' */
	private boolean isWelcomeMessage = false;
	/**
	 * A string builder to append characters to if we are reading in a welcome
	 * message
	 */
	private StringBuilder welcomeMessage;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategy#isMessage
	 * (byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		// first increment the counter
		count++;
		// If this is the first byte of the message
		if (count == 1) {
			// if the message is a one-byte ack message
			if (message == (byte) 0x00 || message == (byte) 0xFF) {
				reset();
				return new byte[] { message };
			}
			// otherwise, save the length bit
			lengthByte = message;
		}
		// if this is the second byte of the message
		else if (count == 2) {
			typeByte = message;
			// if the message is a welcome message
			if (lengthByte == (byte) 'i' && typeByte == (byte) 'i') {
				isWelcomeMessage = true;
				welcomeMessage = new StringBuilder();
				welcomeMessage.append((char) lengthByte);
				welcomeMessage.append((char) typeByte);
			}

			// otherwise, its a normal message
			else {
				bytes = new byte[lengthByte];
				bytes[0] = lengthByte;
				bytes[1] = typeByte;
			}
		}
		// Do this block for every byte after the first two
		else {
			if (!isWelcomeMessage) {
				// add the byte to the array
				bytes[count - 1] = message;
				// check to see if we have received everything.
				if (count == (int) lengthByte) {
					byte[] retVal = bytes;
					reset();
					return retVal;
				}
			}
			// if the message is a welcome message
			else {
				// append every byte to the string builder
				welcomeMessage.append((char) message);
				String wm = welcomeMessage.toString();
				if (wm.endsWith("MODULE")) {
					reset();
					return wm.getBytes();
				}
			}
		}
		// if we have not seen a whole message yet, return null
		return null;
	}

	/**
	 * Resest the state back to empty
	 */
	private void reset() {
		bytes = null;
		lengthByte = -1;
		typeByte = -1;
		count = 0;
		isWelcomeMessage = false;
		welcomeMessage = new StringBuilder();
	}
}
