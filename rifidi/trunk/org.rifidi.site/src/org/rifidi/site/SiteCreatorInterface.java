/* 
 * SiteCreatorInterface.java
 *  Created:	Jul 25, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.site;

import java.util.List;

import org.rifidi.site.creation.BatchCreatorUtility;
import org.rifidi.site.creation.ConfigCreatorUtility;

/**
 * This class acts as a public interface to the other classes to create the
 * batch file and the config.ini file.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class SiteCreatorInterface {

	/**
	 * 
	 */
	private SiteCreatorInterface() {
	}

	/**
	 * 
	 */
	private static SiteCreatorInterface instance = new SiteCreatorInterface();

	/**
	 * 
	 * 
	 * @return
	 */
	public static SiteCreatorInterface getInstance() {
		return instance;
	}

	/**
	 * 
	 * @param siteName
	 */
	public void createBatchFile(String siteName) {
		BatchCreatorUtility.getInstance().createBatchFile(siteName);
	}

	/**
	 * 
	 * @param siteName
	 * @param readerObjectList
	 * @param includeList
	 */
	public void createConfigFile(String siteName,
			List<ReaderObject> readerObjectList, List<String> includeList) {
		ConfigCreatorUtility.getInstance().createConfigFile(siteName,
				readerObjectList, includeList);
	}
}
