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
package org.rifidi.edge.api.resources;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.support.DatabaseMetaDataCallback;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;

/**
 * The Metadata Utils allows you to query information about the database itself.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class MetadataUtils implements DatabaseMetaDataCallback {

	/** The metadata */
	private DatabaseMetaData metaData;
	/** The datasoruce of the DB */
	private DataSource dataSource;

	/**
	 * Set the datasource to the DB
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Test to see if the given tablename exists in the DB
	 * 
	 * @param tableName
	 * @return
	 * @throws SQLException
	 * @throws MetaDataAccessException
	 */
	public synchronized boolean exists(String tableName) throws SQLException,
			MetaDataAccessException {
		JdbcUtils.extractDatabaseMetaData(dataSource, this);
		if (metaData == null) {
			throw new MetaDataAccessException("MetaData is null");
		}
		ResultSet rs = metaData.getTables(metaData.getUserName(), null, null,
				new String[] { "TABLE" });
		while (rs.next()) {
			if (rs.getString(3).equals(tableName))
				return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.jdbc.support.DatabaseMetaDataCallback#processMetaData
	 * (java.sql.DatabaseMetaData)
	 */
	@Override
	public Object processMetaData(DatabaseMetaData dbmd) throws SQLException,
			MetaDataAccessException {
		this.metaData = dbmd;
		return null;
	}

}
