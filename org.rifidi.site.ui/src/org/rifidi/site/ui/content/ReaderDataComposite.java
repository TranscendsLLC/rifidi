/* 
 * ReaderDataComposite.java
 *  Created:	Aug 6, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site.ui.content;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class ReaderDataComposite extends DataComposite {

	/**
	 * 
	 * @param parent
	 * @param style
	 * @param data
	 */
	public ReaderDataComposite(Composite parent, int style,
			CompositeFormData data) {
		super(parent, style, data);

		Button omgButton = new Button(parent, SWT.PUSH);
		omgButton.setText("OMG");

		parent.layout(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.site.ui.content.DataComposite#finalize()
	 */
	@Override
	public void endComposite() {

	}
}
