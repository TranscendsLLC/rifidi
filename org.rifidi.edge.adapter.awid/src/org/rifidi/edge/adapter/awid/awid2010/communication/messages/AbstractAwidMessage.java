/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
