/*
 *  RifidiLogger.java
 *
 *  Created:	Feb 4, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package com.csc.rfid.toolcrib.utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Class that handles the inbound/outbound logging for the CSC.
 * 
 * @author Matthew Dean
 */
public final class RifidiLogger {

	private File file = null;

	public RifidiLogger(String filename) {
		file = new File(filename);
	}

	/**
	 * This method will write a log entry to a given file. If the file doesn't
	 * exist, it will be created.
	 * 
	 * @param tag
	 * @param inbound
	 * @param direction
	 */
	public void writeToFile(String data_to_write) {
		// Check if file exists; if not, create it
		// Write the data to the file

		FileWriter fw = null;

		try {
			if (!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, true);
			fw.write(data_to_write);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
