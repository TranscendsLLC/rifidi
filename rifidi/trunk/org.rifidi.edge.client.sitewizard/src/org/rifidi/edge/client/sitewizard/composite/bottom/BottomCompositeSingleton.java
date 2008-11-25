/* 
 * BottomCompositeSingleton.java
 *  Created:	Aug 5, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.composite.bottom;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.sitewizard.composite.registry.CurrentCompositeRegistry;
import org.rifidi.edge.client.sitewizard.exit.SiteWizardExit;

/**
 * Create the bottom composite
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class BottomCompositeSingleton {

	/**
	 * 
	 */
	private static Log logger = LogFactory
			.getLog(BottomCompositeSingleton.class);

	/**
	 * 
	 */
	private static BottomCompositeSingleton instance = new BottomCompositeSingleton();

	public static final String CREATE_TEXT = "&Create";

	public static final String NEXT_TEXT = " &Next ";

	public static final String FINISH_TEXT = "&Finish";

	/**
	 * 
	 */
	private Button previousCompositeButton = null;

	/**
	 * 
	 */
	private Button nextCompositeButton = null;

	/**
	 * 
	 */
	private Button cancelButton = null;

	/**
	 * @return the instance
	 */
	public static BottomCompositeSingleton getInstance() {
		return instance;
	}

	/**
	 * 
	 */
	private BottomCompositeSingleton() {
	}

	/**
	 * 
	 * 
	 * @return
	 */
	public Composite createBottomComposite(Composite parent) {

		// === Create the new composite ===
		Composite newComp = new Composite(parent, SWT.NONE);
		newComp.setLayout(new GridLayout(3, true));

		// === Next/Prev Button ===
		previousCompositeButton = new Button(newComp, SWT.PUSH);
		previousCompositeButton.setText("&Previous");
		previousCompositeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				CurrentCompositeRegistry.getInstance().showPrevComposite();
			}
		});

		nextCompositeButton = new Button(newComp, SWT.PUSH);
		nextCompositeButton.setText("&Next");
		nextCompositeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DialogContent dc = CurrentCompositeRegistry.getInstance()
						.getCurrentComposite().areYouDone();
				if (dc.isDone()) {
					CurrentCompositeRegistry.getInstance().showNextComposite();
				} else {
					// TODO: Show this in a nag box or something.
					logger.debug("Error: " + dc.getMessage());
				}
			}
		});

		cancelButton = new Button(newComp, SWT.PUSH);
		cancelButton.setText("Cancel");
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				SiteWizardExit.getInstance().exit();
			}
		});

		parent.layout();

		return newComp;
	}

	/**
	 * 
	 * @param textToSet
	 */
	public void setNextButtonText(String textToSet) {
		this.nextCompositeButton.setText(textToSet);
	}

	/**
	 * 
	 * @param textToSet
	 */
	public void setCancelButtonText(String textToSet) {
		this.cancelButton.setText(textToSet);
	}

	/**
	 * 
	 */
	public void setPreviousDisabled() {
		this.previousCompositeButton.setEnabled(false);
	}

	/**
	 * 
	 */
	public void setNextDisabled() {
		this.nextCompositeButton.setEnabled(false);
	}

	/**
	 * 
	 */
	public void setPreviousEnabled() {
		this.previousCompositeButton.setEnabled(true);
	}

	/**
	 * 
	 */
	public void setNextEnabled() {
		this.nextCompositeButton.setEnabled(true);
	}
}
