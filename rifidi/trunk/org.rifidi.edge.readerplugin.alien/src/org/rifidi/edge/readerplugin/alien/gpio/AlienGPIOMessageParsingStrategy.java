package org.rifidi.edge.readerplugin.alien.gpio;

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;

public class AlienGPIOMessageParsingStrategy implements MessageParsingStrategy {

	private static final byte CR = 0x0D;
	private static final byte LF = 0x0A;
	private static final byte NUL = 0x00;
	final StringBuffer sb = new StringBuffer();

	@Override
	public byte[] isMessage(byte message) {
		if (message == NUL) {
			return null;
		}
		if (message == LF) {
			return null;
		}
		if (message == CR) {
			return sb.toString().getBytes();
		}
		sb.append((char) message);
		return null;

	}

}
