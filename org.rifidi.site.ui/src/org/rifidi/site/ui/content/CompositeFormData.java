/* 
 * CompositeFormData.java
 *  Created:	Aug 6, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site.ui.content;

import java.util.HashMap;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class CompositeFormData {

	/**
	 * 
	 */
	public CompositeFormData() {
		stringMap = new HashMap<String, String>();
	}

	/**
	 * @return the stringMap
	 */
	public HashMap<String, String> getStringMap() {
		return stringMap;
	}

	/**
	 * 
	 */
	private HashMap<String, String> stringMap;

}
