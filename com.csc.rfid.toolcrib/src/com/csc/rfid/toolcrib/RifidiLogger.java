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
package com.csc.rfid.toolcrib;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author Matthew Dean
 * 
 */
public class RifidiLogger {

	FileWriter fw = null;

	public static final String CONSTANT_1 = "CTEH-LBLDG-COL-L9";

	public static final String OUTBOUND = "-OUTBOUND";

	public static final String SPACES = "             ";

	public static final String OUTBOUND_SPACES = "    ";

	public RifidiLogger(String filename) {
		try {
			fw = new FileWriter(filename, true);
		} catch (IOException e) {
		}
	}

	/**
	 * @param tag
	 * @param inbound
	 * @param direction
	 */
	public void writeToFile(String tag, boolean inbound, String direction) {
		try {
			if (inbound) {
				fw.write(tag + CONSTANT_1 + SPACES + this.createDate());
			} else {
				fw.write(tag + CONSTANT_1 + OUTBOUND + OUTBOUND_SPACES
						+ this.createDate());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return
	 */
	private String createDate() {
		Date now = new Date(System.currentTimeMillis());
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
		return format.format(now);
	}
}
