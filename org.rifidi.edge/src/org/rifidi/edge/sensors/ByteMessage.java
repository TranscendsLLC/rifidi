/*
 * 
 * ByteMessage.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.sensors;
import org.apache.commons.codec.binary.Hex;

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

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<message.length; i++){
			sb.append(Hex.encodeHex(new byte[]{message[i]}));
			sb.append(' ');
		}
		return sb.toString();
	}
	
	
}
