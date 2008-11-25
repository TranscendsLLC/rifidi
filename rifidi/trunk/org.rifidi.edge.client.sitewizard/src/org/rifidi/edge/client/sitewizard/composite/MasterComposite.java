/* 
 * MasterComposite.java
 *  Created:	Aug 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.composite;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class MasterComposite {

	/**
	 * 
	 */
	private Composite comp = null;

	/**
	 * 
	 */
	private static MasterComposite instance = new MasterComposite();

	/**
	 * 
	 */
	private MasterComposite() {
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public static MasterComposite getInstance() {
		return instance;
	}

	/**
	 * @return the comp
	 */
	public Composite getMasterComposite() {
		return comp;
	}

	/**
	 * @param comp
	 *            the comp to set
	 */
	public void setMasterComposite(Composite comp) {
		this.comp = comp;
	}

	/**
	 * This resets the composite, which will dispose of the main DataComposite
	 * contained in this composite.
	 */
	public void resetMasterComposite() {
		for (Control c : this.comp.getChildren()) {
			c.dispose();
		}
		this.comp.layout();
	}

	/**
	 * Refresh the master composite.  
	 */
	public void layout() {
		this.comp.layout();
	}
}
