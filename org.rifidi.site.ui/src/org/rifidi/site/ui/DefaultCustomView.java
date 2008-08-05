/* 
 * DefaultCustomView.java
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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class DefaultCustomView extends ViewPart {

	/**
	 * 
	 */
	public static final String ID = "org.rifidi.site.ui.defaultcustomview";

	/**
	 * 
	 */
	private ListViewer listViewer;

	/**
	 * 
	 */
	public DefaultCustomView() {
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

		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);

		// 1st Row
		Composite firstRow = new Composite(parent, SWT.NONE);
		GridData firstColumnLayoutData = new GridData(GridData.FILL_BOTH);
		firstColumnLayoutData.widthHint = 200;
		firstRow.setLayoutData(firstColumnLayoutData);
		FillLayout firstColumnLayout = new FillLayout();
		firstColumnLayout.marginHeight = 5;
		firstColumnLayout.marginWidth = 5;
		firstRow.setLayout(firstColumnLayout);

		// 2nd Row
		Composite secondRow = new Composite(parent, SWT.NONE);
		GridData thirdColumnLayoutData = new GridData(
				GridData.HORIZONTAL_ALIGN_END);
		secondRow.setLayoutData(thirdColumnLayoutData);
		GridLayout thirdColumnLayout = new GridLayout(3, false);
		secondRow.setLayout(thirdColumnLayout);

		// === ListViewer ===
		listViewer = new ListViewer(firstRow);

		// === Add and Remove Button ===
		Button executeCommandButton = new Button(secondRow, SWT.PUSH);
		executeCommandButton.setText("< &Previous");
		executeCommandButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});

		Button executePropertyButton = new Button(secondRow, SWT.PUSH);
		executePropertyButton.setText("&Next >");
		executePropertyButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});

		Button stopCurCommandButton = new Button(secondRow, SWT.PUSH);
		stopCurCommandButton.setText("Cancel");
		stopCurCommandButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				
			}
		});
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
