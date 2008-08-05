/* 
 * DefaultCustomList.java
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
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class DefaultCustomList {
	
	/**
	 * 
	 */
	private final static int LIST_SIZE = 2;
	
	/**
	 * 
	 */
	private final static String DEFAULT_TITLE = "Default";
	
	/**
	 * 
	 */
	private final static String DEFAULT_DESCR = "The default configuration";
	
	/**
	 * 
	 */
	private final static String CUSTOM_TITLE = "Custom";
	
	/**
	 * 
	 */
	private final static String CUSTOM_DESCR = "Customize a new configuration";
	
	/**
	 * 
	 */
	private DefaultCustomObject defaultCustomList[] = new DefaultCustomObject[LIST_SIZE];
	
	/**
	 * 
	 */
	public DefaultCustomList() {
		this.initializeDefaultCustomList();
	}
	
	/**
	 * 
	 */
	private void initializeDefaultCustomList() {
		defaultCustomList[0] = new DefaultCustomObject(DEFAULT_TITLE,DEFAULT_DESCR);
		defaultCustomList[1] = new DefaultCustomObject(CUSTOM_TITLE,CUSTOM_DESCR);
	}
}
