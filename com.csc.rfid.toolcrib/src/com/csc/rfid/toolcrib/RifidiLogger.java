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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * @author Matthew Dean
 * 
 */
public final class RifidiLogger {

	private FileWriter fw = null;

	public static final String CONSTANT_1 = "CTEH-LBLDG-COL-L9";

	public static final String OUTBOUND = "-OUTBOUND";

	public static final String SPACES = "             ";

	public static final String OUTBOUND_SPACES = "    ";

	public RifidiLogger(String filename) {
		File file = new File(filename);
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			fw = new FileWriter(file, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param tag
	 * @param inbound
	 * @param direction
	 */
	public void writeToFile(String tag, boolean inbound) {
		if (tag.length() > 12) {
			tag = tag.substring(0, 12);
		}
		tag = tag.toUpperCase();
		try {
			if (inbound) {
				fw.write(tag + CONSTANT_1 + SPACES + this.createDate());
			} else {
				fw.write(tag + CONSTANT_1 + OUTBOUND + OUTBOUND_SPACES
						+ this.createDate());
			}
			fw.write("\r\n");
			fw.flush();
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
