/* 
 * DataComposite.java
 *  Created:	Aug 6, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.composite;

import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.sitewizard.composite.bottom.DialogContent;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public abstract class DataComposite extends Composite {

	/**
	 * 
	 */
	protected Composite parent = null;

	/**
	 * 
	 * 
	 * @param parent
	 * @param style
	 */
	public DataComposite(Composite parent, int style) {
		super(parent, style);
		this.parent = parent;
	}

	/**
	 * 
	 * @return
	 */
	public abstract DataComposite getNextDataComposite();

	/**
	 * 
	 */
	protected abstract void updateNextButton();

	/**
	 * 
	 */
	public void updateButtons() {
		this.updateNextButton();
		this.updatePrevButton();
	}

	/**
	 * 
	 */
	protected abstract void updatePrevButton();

	/**
	 * Remakes the data composite. This method should also re-evaluate the
	 * bottom buttons.
	 * 
	 * @param parent
	 * @param style
	 * @return
	 */
	public abstract DataComposite remakeDataComposite(Composite parent,
			int style);

	/**
	 * Fills out previously entered information
	 */
	public abstract void fillOutInfo();

	/**
	 * 
	 * @return
	 */
	public abstract DialogContent areYouDone();
}
