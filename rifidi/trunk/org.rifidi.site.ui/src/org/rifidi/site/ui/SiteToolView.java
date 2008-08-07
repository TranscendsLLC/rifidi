/* 
 * SiteToolView.java
 *  Created:	Aug 1, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site.ui;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.site.ui.content.CompositeFormData;
import org.rifidi.site.ui.content.CreateBottomComposite;
import org.rifidi.site.ui.content.DataComposite;
import org.rifidi.site.ui.content.DefaultCustomComposite;
import org.rifidi.site.ui.controller.CurrentCompositeRegistry;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class SiteToolView extends ViewPart {

	/**
	 * 
	 */
	public static final String ID = "org.rifidi.site.ui.sitetoolview";

	/**
	 * 
	 */
	private ListViewer listViewer;

	/**
	 * 
	 */
	public SiteToolView() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		System.out.println("Creating the part control");

		GridLayout layout = new GridLayout(1, true);

		parent.setLayout(layout);

		// 1st Row
		DataComposite defaultCustom = new DefaultCustomComposite(parent, SWT.FILL,
				new CompositeFormData());
		
		CurrentCompositeRegistry.getInstance().setCurrentComposite(defaultCustom);

		// 2nd Row
		Composite secondRow = CreateBottomComposite.getInstance()
				.createBottomComposite(parent);
		secondRow.layout();

		parent.layout();

		// === ListViewer ===
		listViewer = new ListViewer(defaultCustom);
	}

	/**
	 * 
	 */
	@SuppressWarnings("unused")
	private void cleanup() {
		listViewer.setInput(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
	}
}
