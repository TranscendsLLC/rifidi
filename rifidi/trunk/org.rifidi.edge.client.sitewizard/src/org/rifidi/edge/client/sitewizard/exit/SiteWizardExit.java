/* 
 * SiteWizardExit.java
 *  Created:	Aug 20, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.exit;

import org.eclipse.swt.widgets.Shell;

/**
 * This class will end the wizard.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class SiteWizardExit {

	/**
	 * 
	 */
	private Shell shell = null;

	/**
	 * 
	 */
	public final static SiteWizardExit instance = new SiteWizardExit();

	/**
	 * 
	 */
	private SiteWizardExit() {
	}

	/**
	 * @return the instance
	 */
	public static SiteWizardExit getInstance() {
		return instance;
	}

	/**
	 * Sets the shell.
	 * 
	 * @param shell
	 */
	public void setShell(Shell shell) {
		this.shell = shell;
	}

	/**
	 * Calling this method will cause the shell to exit.
	 */
	public void exit() {
		if (this.shell != null) {
			this.shell.close();
		}
	}
}
