/* 
 * SiteWizardView.java
 *  Created:	Aug 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.sitewizard.composite.DataComposite;
import org.rifidi.edge.client.sitewizard.composite.DefaultCustomComposite;
import org.rifidi.edge.client.sitewizard.composite.MasterComposite;
import org.rifidi.edge.client.sitewizard.composite.bottom.BottomCompositeSingleton;
import org.rifidi.edge.client.sitewizard.composite.registry.CurrentCompositeRegistry;
import org.rifidi.edge.client.sitewizard.exit.SiteWizardExit;
import org.rifidi.edge.client.sitewizard.message.SiteWizardMessageDisplay;

/**
 * The ViewPart for the SiteWizard.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class SiteWizardView extends ViewPart {

	/**
	 * 
	 */
	private static final String CORE_PLUING_FOLDER = "../../core/plugins/";

	/**
	 * 
	 */
	private static final String FOLDER_NOT_FOUND = "Plugin folder not"
			+ " found, exiting application";

	/**
	 * 
	 */
	private static Log logger = LogFactory.getLog(SiteWizardView.class);

	/**
	 * 
	 */
	public SiteWizardView() {
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
		SiteWizardExit.getInstance().setShell(parent.getShell());
		SiteWizardMessageDisplay.getInstance().setShell(parent.getShell());

		logger.debug("Creating the part control");

		GridLayout layout = new GridLayout(1, true);
		parent.setLayout(layout);

		parent.getShell().setSize(400, 400);

		Composite masterComposite = new Composite(parent, SWT.FILL | SWT.BORDER);
		masterComposite.setLayout(new FillLayout());
		GridData mcgd = new GridData(GridData.FILL_BOTH);
		masterComposite.setLayoutData(mcgd);
		MasterComposite.getInstance().setMasterComposite(masterComposite);

		logger.debug("Creating the defaultcustom");

		// 1st Row
		DataComposite defaultCustom = new DefaultCustomComposite(
				masterComposite, SWT.FILL);

		defaultCustom.layout();

		CurrentCompositeRegistry.getInstance().setFirstComposite(defaultCustom);

		// 2nd Row
		Composite secondRow = BottomCompositeSingleton.getInstance()
				.createBottomComposite(parent);
		secondRow.layout();

		parent.layout();

		File f = new File(CORE_PLUING_FOLDER);

		if (!f.exists()) {
			logger.debug("The folder does not exist");
			SiteWizardMessageDisplay.getInstance().displayMessageBoxAndExit(
					FOLDER_NOT_FOUND);
		} else {
			logger.debug("The folder exists, proceeding...");
		}
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
