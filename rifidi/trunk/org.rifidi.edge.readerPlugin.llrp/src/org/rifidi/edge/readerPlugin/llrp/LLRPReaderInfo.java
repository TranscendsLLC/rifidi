/*
 *  LLRPReaderInfo.java
 *
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.llrp;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * This class represents everything that it needed to connect and authenticate
 * with an LLRP reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPReaderInfo extends AbstractReaderInfo {

	/**
	 * Auto-generates serial UID.
	 */
	private static final long serialVersionUID = 1614519150096139679L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerPlugin.AbstractReaderInfo#getReaderType()
	 */
	@Override
	public String getReaderType() {
		return "LLRP Reader";
	}
}
