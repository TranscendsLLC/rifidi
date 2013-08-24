/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
