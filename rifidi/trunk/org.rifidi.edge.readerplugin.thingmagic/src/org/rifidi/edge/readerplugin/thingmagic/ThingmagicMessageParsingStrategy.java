/*
 *  ThingmagicMessageParsingStrategy.java
 *
 *  Created:	Sep 27, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic;

import java.util.Arrays;

import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategy;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicMessageParsingStrategy implements MessageParsingStrategy {

	/** The message currently being processed. */
	private byte[] _messageBuilder = new byte[0];
	/** Character that terminates a message from alien. */
	public static final char TERMINATION_CHAR = 0x0a;

	private byte lastbyte = 0;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategy#isMessage
	 * (byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		if (TERMINATION_CHAR == message && _messageBuilder.length == 0) {
			lastbyte = 0;
			return _messageBuilder;
		}
		// FIXME: This isn't perfect and might not work
		if (TERMINATION_CHAR == message && lastbyte == TERMINATION_CHAR) {
			lastbyte = 0;
			byte[] ret = _messageBuilder;
			_messageBuilder = new byte[0];
			return ret;
		}
		_messageBuilder = Arrays.copyOf(_messageBuilder,
				_messageBuilder.length + 1);
		_messageBuilder[_messageBuilder.length - 1] = message;
		lastbyte = message;
		return null;
	}

}
