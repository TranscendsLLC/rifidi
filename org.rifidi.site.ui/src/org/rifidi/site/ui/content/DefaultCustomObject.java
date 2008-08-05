/* 
 * DefaultCustomObject.java
 *  Created:	Aug 5, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site.ui.content;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class DefaultCustomObject {

	private String title;
	private String description;

	/**
	 * 
	 */
	public DefaultCustomObject(String title, String description) {
		this.title = title;
		this.description = description;
	}

	/**
	 * @return the title
	 */
	public String getTitleAsString() {
		return title;
	}

	/**
	 * @return the description
	 */
	public String getDescriptionAsString() {
		return description;
	}
}
