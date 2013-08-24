/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
