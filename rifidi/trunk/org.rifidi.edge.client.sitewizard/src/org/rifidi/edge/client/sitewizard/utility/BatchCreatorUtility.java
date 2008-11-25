/* 
 * BatchCreatorUtility.java
 *  Created:	Jul 23, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.rifidi.edge.client.sitewizard.constants.ConfigurationConstants;

/**
 * This class creates the files and folders that
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class BatchCreatorUtility {

	/**
	 * The beginning of the batch file.
	 */
	private static final String BATCH_BEGIN = "java -jar ../../core/plugins/"
			+ "org.eclipse.osgi_3.3.1.R33x_v20070828.jar "
			+ "-console -configuration ../../sites/";
	/**
	 * The end of the batch file.
	 */
	private static final String BATCH_END = "/configuration";

	/**
	 * 
	 */
	private static final String FORWARD_SLASH = "/";

	/**
	 * 
	 */
	private static final String START_BAT = "/start";

	private static final String END_BAT = ".bat";

	/**
	 * Private singleton instance.
	 */
	private static BatchCreatorUtility instance = new BatchCreatorUtility();

	/**
	 * Private constructor
	 */
	private BatchCreatorUtility() {
	}

	/**
	 * Returns the singleton instance.
	 * 
	 * @return
	 */
	public static BatchCreatorUtility getInstance() {
		return instance;
	}

	/**
	 * Create the folders
	 * 
	 * @param siteName
	 */
	private void createFolders(String siteName) {
		File f = new File(ConfigurationConstants.SERVER_FOLDER + siteName
				+ FORWARD_SLASH);
		System.out.println("File path: " + f.getAbsolutePath());
		f.mkdirs();
	}

	/**
	 * 
	 * TODO: potential weirdness if the string contains special characters, be
	 * sure to only allow alphanumeric from UI perspective.
	 * 
	 * @param siteName
	 */
	public void createBatchFile(String siteName) {
		this.createFolders(siteName);
		File f = new File(ConfigurationConstants.SERVER_FOLDER + siteName
				+ START_BAT + siteName + END_BAT);
		FileWriter fwriter = null;
		try {
			f.createNewFile();
			fwriter = new FileWriter(f);
			fwriter.write(BATCH_BEGIN + siteName + BATCH_END);
			fwriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fwriter != null) {
					fwriter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
