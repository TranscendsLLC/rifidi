/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import java.util.Arrays;

import org.rifidi.edge.core.sensors.base.threads.MessageParser;

/**
 * This object parses alien messages that come in byte by byte, for example from
 * a socket. When an entire message has been received, it returns the message.
 * Note that it keeps an internal state, so there is a one to one correspondence
 * between this message parser and an input source (such as a socket). In other
 * words, create a new instance of this object for each socket that is created
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienMessageParser implements MessageParser {

	/** The message currently being processed. */
	private byte[] _messageBuilder = new byte[0];
	/** Character that terminates a message from alien. */
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
		return null;
	}

}
