package org.rifidi.edge.core.readerplugin.protocol;

import org.rifidi.edge.core.exceptions.RifidiInvalidMessageFormat;

/**
 * The CommunicationProtocol can convert Messages of a specific reader format
 * into bytes and vice versa. It is used in the communication to send specific
 * reader messages of a reader plugin to the reader over the socket and receive
 * the responses.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface CommunicationProtocol {

	/**
	 * Convert from bytes to a message in the format of the reader. The Protocol
	 * keeps piling up bytes until the message is complete then it returns the
	 * message.
	 * 
	 * @param b
	 *            Byte to add to the Message
	 * @return the whole message or null if the message is not complete yet
	 * @throws RifidiInvalidMessageFormat
	 *             if unable to convert
	 */
	public Object byteToMessage(byte b) throws RifidiInvalidMessageFormat;

	/**
	 * Convert a message in the specific reader format into a array of bytes.
	 * 
	 * @param message
	 *            message to convert
	 * @return byte array representing the message
	 * @throws RifidiInvalidMessageFormat
	 *             if unable to convert
	 */
	public byte[] messageToByte(Object message)
			throws RifidiInvalidMessageFormat;
}
