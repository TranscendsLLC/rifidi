/*
 * AbstractAwidMessage.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.communication.messages;

import org.apache.commons.codec.binary.Hex;

/**
 * An AwidMessage is an array of bytes coming from the Awid reader. 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AbstractAwidMessage {

	/**The array of bytes*/
	protected final byte[] rawmessage;

	/**
	 * @param rawmessage
	 */
	public AbstractAwidMessage(byte[] rawmessage) {
		this.rawmessage = rawmessage;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<rawmessage.length; i++){
			sb.append(Hex.encodeHex(new byte[]{rawmessage[i]}));
			sb.append(' ');
		}
		return sb.toString();
	}

}
