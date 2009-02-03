/* 
 * AlienMessageProtocol.java
 *  Created:	Jul 10, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien.protocol;

import org.rifidi.edge.core.api.readerplugin.MessageProtocol;

/**
 * The protocol that will convert messages from Strings to XML data. Currently
 * unused.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AlienMessageProtocol implements MessageProtocol {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.protocol.MessageProtocol#toXML(java.lang.Object)
	 */
	@Override
	public String toXML(Object message) {

		return null;
	}

}
