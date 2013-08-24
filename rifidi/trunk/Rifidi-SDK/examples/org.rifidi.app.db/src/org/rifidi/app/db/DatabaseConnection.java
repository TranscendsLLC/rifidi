/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
				+ datestr + "');";
		try {
			this.jdbcTemplate.execute(command);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
