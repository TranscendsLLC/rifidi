package org.rifidi.edge.readerplugin.alien.gpio;

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;

/**
 * This is an implementation of the MessageParsingStrategy for the Alien
 * IOStream. Each message is delimited by a newline ([CR] [LF]). The NUL
 * character is tacked on if it is the end of a transmission.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienGPIOMessageParsingStrategy implements MessageParsingStrategy {

	/** The Carraige Return byte */
	private static final byte CR = 0x0D;
	/** The Line Feed byte */
	private static final byte LF = 0x0A;
	/** The Null byte */
	private static final byte NUL = 0x00;
	/** A string buffer for building up the message */
	final StringBuffer sb = new StringBuffer();
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy#isMessage(byte)
	 */
	@Override
	public byte[] isMessage(byte message) {
		// Return null if we see the Null byte */
		if (message == NUL) {
			return null;
		}

		// Return null if we see LF, because we already returned the messagewhen
		// we saw the CR

		if (message == LF) {
			return null;
		}

		// return the message whe we see the CR character
		if (message == CR) {
			return sb.toString().getBytes();
		}
		
		// if we didnt see the NUL, LR, or CR characters, append the byte to the
		// message and return null
		sb.append((char) message);
		return null;

	}



}
