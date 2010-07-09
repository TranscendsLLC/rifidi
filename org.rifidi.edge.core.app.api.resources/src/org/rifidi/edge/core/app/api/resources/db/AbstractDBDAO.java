/**
 * 
 */
package org.rifidi.edge.core.app.api.resources.db;

import java.sql.SQLException;
import java.util.Properties;

import org.rifidi.edge.core.app.api.resources.CannotCreateResourceException;
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
	 * Get a command to create a table. If this returns a string, this string
	 * will be executed if the table listed in getTableName() does not exist. If
	 * this returns null, the table will not be created.
	 * 
	 * @return
	 */
	protected String getCreateTableSQL() {
		return null;
	}

	/**
	 * Return the name of the table this DAO interacts with
	 * 
	 * @return
	 */
	protected abstract String getTableName();

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
			if (!metadataUtils.exists(getTableName())
					&& getCreateTableSQL() != null) {
				this.jdbcTemplate.getJdbcOperations().execute(
						getCreateTableSQL());
			}

		} catch (DataAccessException e) {
			e.printStackTrace();
			throw new IllegalStateException("DBDAO cannot be initialized",e);
		} catch (MetaDataAccessException e) {
			e.printStackTrace();
			throw new IllegalStateException("DBDAO cannot be initialized",e);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalStateException("DBDAO cannot be initialized",e);
		} catch (CannotCreateResourceException e) {
			e.printStackTrace();
			throw new IllegalStateException("DBDAO cannot be initialized",e);
		}

	}

}
