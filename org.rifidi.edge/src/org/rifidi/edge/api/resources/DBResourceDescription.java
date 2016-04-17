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

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSourceFactory;

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
