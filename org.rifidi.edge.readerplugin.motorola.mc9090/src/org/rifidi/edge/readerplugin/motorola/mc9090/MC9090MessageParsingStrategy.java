/**
 * 
 */
package org.rifidi.edge.readerplugin.motorola.mc9090;

import java.util.ArrayList;

import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategy;

/**
 * A Strategy that parses messages from the MC9090 Reader. MC9090 message are
 * terminated with \0.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class MC9090MessageParsingStrategy implements MessageParsingStrategy {

	/** The recieved message */
	private ArrayList<Byte> message;

	/**
	 * Constructor
	 */
	public MC9090MessageParsingStrategy() {
		message = new ArrayList<Byte>();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategy#isMessage
	 * (byte)
	 */
	@Override
	public byte[] isMessage(byte _byte) {
		// if we have seen the termination character
		if (_byte == (byte) '\0') {
			// create a new byte array, and add the message seen so far
			byte[] retVal = new byte[message.size()];
			for (int i = 0; i < message.size(); i++) {
				retVal[i] = message.get(i);
			}
			// clear the old message
			message.clear();
			return retVal;
		} else {
			// if we have not seen the termination character add it to the
			// message
			this.message.add(_byte);
			return null;
		}

	}

}
