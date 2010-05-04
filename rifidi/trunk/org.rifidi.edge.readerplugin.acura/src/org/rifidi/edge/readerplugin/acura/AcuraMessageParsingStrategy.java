/*
 *  AcuraMessageParsingStrategy.java
 *
 *  Created:	Dec 8, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.acura;

import java.util.ArrayList;

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;

/**
 * Parsing strategy for the
 * 
 * @author Matthew Dean
 */
public class AcuraMessageParsingStrategy implements MessageParsingStrategy {

	private ArrayList<Byte> messageBuilder = new ArrayList<Byte>();

	// TODO: It is possible we should monitor for start/end bytes instead of
	// just going on length. For the moment, it doesn't matter.
	private final int LENGTH = 14;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.MessageParser#isMessage(byte)
	 */
	@Override
	public byte[] isMessage(byte nextByte) {
		messageBuilder.add(nextByte);
		if (messageBuilder.size() == LENGTH) {
			byte[] retval = new byte[LENGTH];
			int index = 0;
			for (Byte b : messageBuilder) {
				retval[index] = b;
			}
			return retval;
		}
		return null;
	}

	// private void printByteMessage(byte[] message) {
	// for(byte b:message) {
	// System.out.print(ByteAndHexConvertingUtility.toHexString(b) + " ");
	// }
	// }

}
