/*
 *  Activator.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerPlugin.thingmagic;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

public class ThingMagicReaderInfo extends AbstractReaderInfo {

	
	/**
	 * Serializable id.
	 */
	private static final long serialVersionUID = -609667495940483193L;

	@Override
	public String getReaderType() {
		return "ThingMagicReader";
	}

}