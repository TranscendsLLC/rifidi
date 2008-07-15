/* 
 * AlienCommunicationProtocol.java
 *  Created:	Jul 9, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien.protocol;

import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienCommunicationProtocol implements CommunicationProtocol {

	private static final char TERMINATION_CHAR = '\0';

	private StringBuilder buf = new StringBuilder();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol#byteToMessage(byte)
	 */
	@Override
	public Object byteToMessage(byte b) {
		buf.append((char) b);
		if ((char) b == AlienCommunicationProtocol.TERMINATION_CHAR) {
			String temp = buf.toString();
			buf = new StringBuilder();
			return temp;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol#messageToByte(java.lang.Object)
	 */
	@Override
	public byte[] messageToByte(Object message) {
		//TODO: Possibly add a \n to the protocol automatically
		return ((String) message).getBytes();
	}

}
