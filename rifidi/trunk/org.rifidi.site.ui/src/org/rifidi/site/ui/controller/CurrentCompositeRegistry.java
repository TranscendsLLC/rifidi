/* 
 * CurrentCompositeRegistry.java
 *  Created:	Aug 6, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site.ui.controller;

import org.rifidi.site.ui.content.DataComposite;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class CurrentCompositeRegistry {

	/**
	 * 
	 */
	public final static int CUSTOM_SELECTION = 0;

	/**
	 * 
	 */
	public final static int DEFAULT_SELECTION = 1;

	/**
	 * 
	 */
	private DataComposite curComp = null;

	/**
	 * 
	 */
	private static CurrentCompositeRegistry instance = new CurrentCompositeRegistry();

	/**
	 * 
	 */
	private CurrentCompositeRegistry() {

	}

	/**
	 * @return the instance
	 */
	public static CurrentCompositeRegistry getInstance() {
		return instance;
	}

	/**
	 * 
	 * @return
	 */
	public DataComposite getCurrentComposite() {
		return curComp;
	}

	/**
	 * 
	 * @param comp
	 */
	public void setCurrentComposite(DataComposite comp) {
		curComp = comp;
	}
	
	/**
	 * 
	 * 
	 * @param comp
	 * @param selection
	 * @return
	 */
	public void showNextComposite() {
		curComp.endComposite();
	}
}
