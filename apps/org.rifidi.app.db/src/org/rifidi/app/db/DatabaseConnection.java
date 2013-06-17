/*
 *  DatabaseConnection.java
 *
 *  Created:	May 26, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.app.db;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.rifidi.edge.notification.TagReadEvent;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 */
public class DatabaseConnection {

	private JdbcTemplate jdbcTemplate;

	private static final String USE_COMMAND = "use db;";

	private static final SimpleDateFormat FORMATTER = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	/**
	 * 
	 */
	public DatabaseConnection(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Opens the connection with the database.
	 */
	public void init() {
		// use the database 'db'
		this.jdbcTemplate.execute(USE_COMMAND);
	}

	/**
	 * 
	 */
	public synchronized void arrivedEvent(TagReadEvent tag) {
		String epc = tag.getTag().getFormattedID();
		Date date = new Date(tag.getTimestamp());
		String datestr = FORMATTER.format(date);
		String command = "INSERT INTO assets VALUES('" + epc + "', '"
				+ tag.getReaderID() + "', '" + tag.getAntennaID() + "', '"
				+ datestr + "', 'ARRIVED');";
		try {
			this.jdbcTemplate.execute(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void departedEvent(TagReadEvent tag) {
		String epc = tag.getTag().getFormattedID();
		Date date = new Date(System.currentTimeMillis());
		String datestr = FORMATTER.format(date);
		String command = "INSERT INTO assets VALUES('" + epc + "', '"
				+ tag.getReaderID() + "', '" + tag.getAntennaID() + "', '"
				+ datestr + "', 'DEPARTED');";
		try {
			this.jdbcTemplate.execute(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
