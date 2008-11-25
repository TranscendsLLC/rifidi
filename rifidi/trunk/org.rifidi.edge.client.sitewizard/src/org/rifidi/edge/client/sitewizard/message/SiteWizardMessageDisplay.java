/* 
 * SiteWizardMessageDisplay.java
 *  Created:	Aug 22, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.message;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class SiteWizardMessageDisplay {

	/**
	 * 
	 */
	private static Log logger = LogFactory.getLog(SiteWizardMessageDisplay.class);
	
	/**
	 * 
	 */
	private static SiteWizardMessageDisplay instance = new SiteWizardMessageDisplay();

	/**
	 * 
	 */
	private Shell shell = null;

	/**
	 * 
	 */
	private SiteWizardMessageDisplay() {
	}

	/**
	 * @return the instance
	 */
	public static SiteWizardMessageDisplay getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param shell
	 */
	public void setShell(Shell shell) {
		this.shell = shell;
	}

	/**
	 * 
	 * @param messageToDisplay
	 */
	public void displayMessageBoxAndExit(String messageToDisplay) {
		logger.debug("Displaying a message: " + this.shell);
		
		MessageBox mb = new MessageBox(this.shell, SWT.OK);
		mb.setMessage(messageToDisplay);
		mb.open();
		this.shell.close();
	}

	/**
	 * 
	 * @param messageToDisplay
	 */
	public void displayMessageBox(String messageToDisplay) {
		MessageBox mb = new MessageBox(this.shell, SWT.OK);
		mb.setMessage(messageToDisplay);
		mb.open();
	}
}
