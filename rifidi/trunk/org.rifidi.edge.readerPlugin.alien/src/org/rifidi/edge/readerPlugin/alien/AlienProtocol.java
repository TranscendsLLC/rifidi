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

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.communication.Protocol;

/**
 * Protocol class for the Alien reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienProtocol extends Protocol {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.buffer.Protocol#fromObject(java.lang.Object)
	 */
	@Override
	public byte[] fromObject(Object arg) {
		return ((String) arg).getBytes();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.buffer.Protocol#toObject(byte[])
	 */
	@Override
	public List<Object> toObject(byte[] arg) {
		List<Object> retVal = new ArrayList<Object>();

		String input = new String(arg);
		String[] splitstr = input.split("\0");

		for (String s : splitstr) {
			if (s.length() > 0) {
				retVal.add(s);
			}
		}

		return retVal;
	}

}
