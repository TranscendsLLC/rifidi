/* 
 * AlienProtocol.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.alien;

import org.rifidi.edge.core.communication.protocol.Protocol;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;

/**
 * Protocol class for the Alien reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienProtocol extends Protocol {

	private static final char TERMINATION_CHAR = '\0';

	private StringBuilder buf;

	/**
	 * 
	 */
	public AlienProtocol() {
		buf = new StringBuilder();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.protocol.Protocol#add(byte)
	 */
	@Override
	public Object add(byte b) throws RifidiInvalidMessageFormat {
		buf.append((char) b);
		if ((char) b == AlienProtocol.TERMINATION_CHAR) {
			String temp = buf.toString();
			buf = new StringBuilder();
			return temp;
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.protocol.Protocol#toByteArray(java.lang.Object)
	 */
	@Override
	public byte[] toByteArray(Object o) throws RifidiInvalidMessageFormat {
		return ((String) o).getBytes();
	}

	// /*
	// * (non-Javadoc)
	// *
	// * @see
	// org.rifidi.edge.core.communication.buffer.Protocol#toObject(byte[])
	// */
	// @Override
	// public List<Object> toObject(byte[] arg) {
	// List<Object> retVal = new ArrayList<Object>();
	//
	// String input = new String(arg);
	//
	// if (!input.equals("\0")) {
	// String[] splitstr = input.split("\0");
	//
	// for (String s : splitstr) {
	// s = s.trim();
	// if (s.length() > 0) {
	// retVal.add(s);
	// }
	// }
	// } else {
	// retVal.add("\0");
	// }
	//
	// return retVal;
	// }
}
