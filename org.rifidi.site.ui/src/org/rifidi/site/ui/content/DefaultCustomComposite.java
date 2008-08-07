/* 
 * DefaultCustomComposite.java
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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class DefaultCustomComposite extends DataComposite {

	private Composite parent = null;

	/**
	 * 
	 * @param parent
	 * @param style
	 * @param formData
	 */
	public DefaultCustomComposite(Composite parent, int style,
			CompositeFormData formData) {
		super(parent, style, formData);

		this.parent = parent;

		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = SWT.FILL;

		this.setLayout(layout);

		Button radio1 = new Button(this, SWT.RADIO);
		radio1.setText("Default");

		Label desc1 = new Label(this, SWT.NONE);
		desc1
				.setText("Create an edge server with default settings and all readers.");

		Button radio2 = new Button(this, SWT.RADIO);
		radio2.setText("Custom");

		Label desc2 = new Label(this, SWT.NONE);
		desc2.setText("Create an edge server with custom settings.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.site.ui.content.DataComposite#endComposite()
	 */
	@Override
	public void endComposite() {
		this.dispose();

		ReaderDataComposite rdc = new ReaderDataComposite(parent, SWT.NONE,
				new CompositeFormData());
		rdc.layout(true);
	}

}
