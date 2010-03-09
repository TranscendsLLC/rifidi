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
 * Class that handles the inbound/outbound logging for the CSC. This class
 * currently handles the SAP log, the standard log, the ghost log, and the
 * downtime log.
 * 
 * @author Matthew Dean
 */
public final class RifidiLogger {

	/**
	 * Constrcutor.
	 * 
	 * @param filenamePrefix
	 */
	public RifidiLogger() {
	}

	/**
	 * 
	 * 
	 * @param entry_to_write
	 */
	public synchronized void writeToSAPLog(String data_to_write) {
		FileWriter fw = null;
		try {
			File file = new File(System.getProperty("com.csc.saplogfile"));
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				fw.write(data_to_write);
				fw.flush();
			}
		} catch (IOException e) {
			// TODO: Handle this
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
	 * 
	 * @param entry_to_write
	 */
	public synchronized void writeToStandardLog(String data_to_write) {
		FileWriter fw = null;
		try {
			File file = new File(System.getProperty("com.csc.standardlog"));
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				fw.write(data_to_write);
				fw.flush();
			}
		} catch (IOException e) {
			// TODO: Handle this
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
	 * 
	 * @param entry_to_write
	 */
	public synchronized void writeToGhostLog(String data_to_write) {
		FileWriter fw = null;
		try {
			File file = new File(System.getProperty("com.csc.ghostlog"));
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				fw.write(data_to_write);
				fw.flush();
			}
		} catch (IOException e) {
			// TODO: Handle this
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
	 * 
	 * @param entry_to_write
	 */
	public synchronized void writeDowntimeLog(String data_to_write) {
		FileWriter fw = null;
		try {
			File file = new File(System.getProperty("com.csc.downtimelog"));
			if (!file.exists()) {
				file.createNewFile();

				fw = new FileWriter(file, true);
				fw.write(data_to_write);
				fw.flush();
			}
		} catch (IOException e) {
			// TODO: Handle this
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
}
