/**
 * 
 */
package org.rifidi.edge.core.app.api.resources.db;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.rifidi.edge.core.app.api.resources.CannotCreateResourceException;
import org.rifidi.edge.core.app.api.resources.ResourceDescription;

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
