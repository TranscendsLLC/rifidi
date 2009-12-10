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

import java.util.Arrays;

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.readerplugin.acura.utilities.ByteAndHexConvertingUtility;

/**
 * @author Matthew Dean
 *
 */
public class AcuraMessageParsingStrategy implements MessageParsingStrategy {

	/** The message currently being processed. */
	private byte[] _messageBuilder = new byte[0];
	/** Character that terminates a message from acura. */
	public static final char TERMINATION_CHAR = '\0';

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.MessageParser#isMessage(byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		if (TERMINATION_CHAR == message) {
			byte[] ret = _messageBuilder;
			_messageBuilder = new byte[0];
			return ret;
		}
		_messageBuilder = Arrays.copyOf(_messageBuilder,
				_messageBuilder.length + 1);
		_messageBuilder[_messageBuilder.length - 1] = message;
		this.printByteMessage(_messageBuilder);
		return null;
	}
	
	
	private void printByteMessage(byte[] message) {
		for(byte b:message) {
			System.out.print(ByteAndHexConvertingUtility.toHexString(b) + " ");
		}
	}

}
