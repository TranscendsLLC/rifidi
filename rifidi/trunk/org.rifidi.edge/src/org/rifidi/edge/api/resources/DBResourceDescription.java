/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.resources;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;

/**
 * The DBResourceDescription describes a database to connect to
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class DBResourceDescription extends ResourceDescription implements
		DBProperties {

	/**
	 * Creates a DataSource object from the given properties
	 * 
	 * @return
	 * @throws CannotCreateResourceException
	 */
	DataSource getDataSource() throws CannotCreateResourceException {

		try {
			return BasicDataSourceFactory.createDataSource(properties);
		} catch (Exception e) {
			throw new CannotCreateResourceException(e);
		}
	}

}
