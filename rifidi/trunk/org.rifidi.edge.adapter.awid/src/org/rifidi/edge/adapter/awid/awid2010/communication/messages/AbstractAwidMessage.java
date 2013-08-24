/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.awid.awid2010.communication.messages;

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
