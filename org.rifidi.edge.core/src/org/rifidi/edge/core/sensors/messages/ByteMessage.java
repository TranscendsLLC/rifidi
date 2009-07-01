/**
 * 
 */
package org.rifidi.edge.core.sensors.messages;

/**
 * A wrapper around a byte array for sending and receiving messages to and from
 * a reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ByteMessage {
	public byte[] message;

	/**
	 * Constructor.
	 * 
	 * @param message
	 */
	public ByteMessage(byte[] message) {
		this.message = message;
	}
}
