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
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Class that handles the inbound/outbound logging for the CSC. This class
 * currently handles the SAP log, the standard log, the ghost log, and the
 * downtime log.
 * 
 * @author Matthew Dean
 */
public final class RifidiLogger {

	private String suffix;

	private static final String HYPHEN = "-";

	private String filenamePrefix = null;

	private String currentFilename = null;

	private File file = null;
	
	private boolean appendTimestamp = false;

	/**
	 * Constrcutor.
	 * 
	 * @param filenamePrefix
	 */
	public RifidiLogger(String filenamePrefix, String suffix,
			boolean appendTimestamp) {
		this.filenamePrefix = filenamePrefix;
		this.suffix = suffix;
		this.appendTimestamp = appendTimestamp;
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
		if (currentFilename != null) {
			file = new File(this.currentFilename);
		}

		try {
			if (file == null || !file.exists()) {
				String newFileName = this
						.createTimestampFile(this.filenamePrefix);
				file = new File(newFileName);
				if(!file.exists()){
					file.createNewFile();
				}
				this.currentFilename = newFileName;
			}

			fw = new FileWriter(file, true);
			fw.write(data_to_write);
			fw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				// Don't care
			}
		}
	}

	/**
	 * 
	 */
	private String createTimestampFile(String prefix) {
		String filename; 
		if(appendTimestamp){
			filename = prefix + HYPHEN + this.createDate() + this.suffix;
			
		}else{
			filename = prefix + this.suffix;
		}
		return filename;
	}

	/*
	 * Returns the current time as a date in yyyyMMddHHmmss format.
	 */
	private String createDate() {
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(now);
	}
}
