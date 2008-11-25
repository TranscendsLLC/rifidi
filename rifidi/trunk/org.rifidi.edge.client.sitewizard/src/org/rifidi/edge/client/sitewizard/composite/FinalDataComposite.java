/* 
 * FinalDataComposite.java
 *  Created:	Aug 13, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.composite;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.rifidi.edge.client.sitewizard.composite.bottom.BottomCompositeSingleton;
import org.rifidi.edge.client.sitewizard.composite.bottom.DialogContent;

/**
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class FinalDataComposite extends DataComposite {

	/**
	 * 
	 * @param parent
	 * @param style
	 */
	public FinalDataComposite(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new GridLayout(1, false));

		Label message = new Label(this, SWT.NONE);
		message.setText("The site has been successfully created");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#areYouDone()
	 */
	@Override
	public DialogContent areYouDone() {
		return new DialogContent(true, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sitewizard.composite.DataComposite#
	 * getNextDataComposite()
	 */
	@Override
	public DataComposite getNextDataComposite() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#remakeDataComposite
	 * (org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public DataComposite remakeDataComposite(Composite parent, int style) {
		// Not necessary, as there is nothing after the "final" screen.  
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#updateNextButton
	 * ()
	 */
	@Override
	public void updateNextButton() {
		BottomCompositeSingleton.getInstance().setCancelButtonText(
				BottomCompositeSingleton.FINISH_TEXT);
		BottomCompositeSingleton.getInstance().setNextDisabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#updatePrevButton
	 * ()
	 */
	@Override
	public void updatePrevButton() {
		BottomCompositeSingleton.getInstance().setPreviousDisabled();
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.client.sitewizard.composite.DataComposite#fillOutInfo()
	 */
	@Override
	public void fillOutInfo() {
		//Nothing
	}

}
