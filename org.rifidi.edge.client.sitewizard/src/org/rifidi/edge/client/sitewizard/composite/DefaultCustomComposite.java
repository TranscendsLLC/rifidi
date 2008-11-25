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
package org.rifidi.edge.client.sitewizard.composite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.rifidi.edge.client.sitewizard.composite.bottom.BottomCompositeSingleton;
import org.rifidi.edge.client.sitewizard.composite.bottom.DialogContent;
import org.rifidi.edge.client.sitewizard.composite.data.SiteWizardData;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class DefaultCustomComposite extends DataComposite {

	/**
	 * 
	 */
	private static Log logger = LogFactory.getLog(DefaultCustomComposite.class);

	Button custom_button = null;

	Button default_button = null;

	/**
	 * 
	 * 
	 * @param parent
	 * @param style
	 * @param formData
	 */
	public DefaultCustomComposite(Composite parent, int style) {
		super(parent, style);

		logger.debug("creating a defaultcustomcomposite");

		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = SWT.FILL;

		this.setLayout(layout);

		default_button = new Button(this, SWT.RADIO);
		default_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SiteWizardData.getInstance().setCustom(false);
			}
		});
		default_button.setText("Default");
		
		Label desc2 = new Label(this, SWT.NONE);
		desc2.setText("Create an edge server with custom settings.");
		
		custom_button = new Button(this, SWT.RADIO);
		custom_button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SiteWizardData.getInstance().setCustom(true);
			}
		});
		custom_button.setText("Custom");
		
		Label desc1 = new Label(this, SWT.NONE);
		desc1.setText("Create an edge server with default"
				+ " settings and all readers.");

		this.layout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sitewizard.composite.DataComposite#
	 * getNextDataComposite()
	 */
	@Override
	public DataComposite getNextDataComposite() {
		logger.debug("Getting the next composite in the defaultcustom.");
		return new NameDataComposite(parent, SWT.NONE);
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

	@Override
	public DataComposite remakeDataComposite(Composite parent, int style) {
		return new DefaultCustomComposite(parent, style);
	}

	/**
	 * 
	 */
	@Override
	public void updatePrevButton() {
		BottomCompositeSingleton.getInstance().setPreviousDisabled();
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
		if (this.areYouDone().isDone()) {
			BottomCompositeSingleton.getInstance().setNextEnabled();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#fillOutInfo()
	 */
	@Override
	public void fillOutInfo() {
		if (SiteWizardData.getInstance().isCustom()) {
			this.custom_button.setSelection(true);
		} else {
			this.default_button.setSelection(true);
		}
	}
}
