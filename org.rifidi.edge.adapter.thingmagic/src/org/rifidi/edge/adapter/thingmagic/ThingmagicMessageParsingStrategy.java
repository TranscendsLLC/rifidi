/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic;

import java.util.Arrays;

import org.rifidi.edge.sensors.sessions.MessageParsingStrategy;

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
	 * org.rifidi.edge.sensors.base.threads.MessageParsingStrategy#isMessage
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
