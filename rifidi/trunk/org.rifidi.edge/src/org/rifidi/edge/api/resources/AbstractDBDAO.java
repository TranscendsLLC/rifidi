/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.resources;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.support.MetaDataAccessException;

/**
 * This is an abstract DAO that can be used as a super class for concrete DAOs.
 * It is useful for DAOs which interact with a single table.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractDBDAO {

	/** The JDBC template to use */
	protected SimpleJdbcTemplate jdbcTemplate;

	/**
	 * Get the connection properties
	 * 
	 * @return
	 */
	protected abstract Properties getProperties();

	/**
	 * Returns a Hashmap where the key is a table name, and the value is an SQL
	 * string that creates a table. The create statement will be executed when
	 * this DAO starts up if the table does not already exist.
	 * 
	 * @return
	 */
	protected HashMap<String, String> getCreateTableSQL() {
		return new HashMap<String, String>();
	}

	/**
	 * Set the DBResoruce service. It get a hold of the JDBCTemplate and create
	 * the table if necessary
	 * 
	 * @param service
	 */
	public void setDBResourceService(DBResourceService service) {
		DBResourceDescription descr = new DBResourceDescription();
		descr.setProperties(getProperties());

		try {
			this.jdbcTemplate = service.getResource(descr);
			MetadataUtils metadataUtils = service.getMetadataUtils(descr);
			HashMap<String, String> createStatements = getCreateTableSQL();
			for (String tableName : createStatements.keySet()) {
				if (!metadataUtils.exists(tableName)
						&& getCreateTableSQL() != null) {
					this.jdbcTemplate.getJdbcOperations().execute(
							createStatements.get(tableName));
				}
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new IllegalStateException("DBDAO cannot be initialized", e);
		} catch (MetaDataAccessException e) {
			e.printStackTrace();
			throw new IllegalStateException("DBDAO cannot be initialized", e);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException("DBDAO cannot be initialized", e);
		} catch (CannotCreateResourceException e) {
			e.printStackTrace();
			throw new IllegalStateException("DBDAO cannot be initialized", e);
		}

	}

}
