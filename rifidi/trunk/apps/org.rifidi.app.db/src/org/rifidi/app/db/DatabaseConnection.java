/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
