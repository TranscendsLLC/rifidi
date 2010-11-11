/*
 * AckMessage.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
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
