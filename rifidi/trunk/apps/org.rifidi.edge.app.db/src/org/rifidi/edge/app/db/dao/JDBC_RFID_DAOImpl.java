/**
 * 
 */
package org.rifidi.edge.app.db.dao;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.app.db.domain.RFIDEvent;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * This class is an implementation the RFID_DAO interface that uses JDBC to
 * write to a datasource
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class JDBC_RFID_DAOImpl implements RFID_DAO {

	/** A spring template that makes it easy to used JDBC */
	private SimpleJdbcTemplate template;
	/** A spring object that makes it easy to write to a db */
	private SimpleJdbcInsert insertRFIDEvent;
	/** The data source of the DB */
	private SimpleDriverDataSource ds;
	/** The logger for this class. */
	private Log logger = LogFactory.getLog(JDBC_RFID_DAOImpl.class);
	/** The name of the table to write to */
	private static final String TABLE = "rfidevents";
	/** The name of the ID column in the table */
	private static final String COL_ID = "id";
	/** The name of the reader column in the table */
	private static final String COL_READER = "reader";
	/** The name of the antenna column in the table */
	private static final String COL_ANT = "antenna";
	/** The name of the time column in the table */
	private static final String COL_TIME = "time";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.app.db.dao.RFIDDAO#addRow(org.rifidi.edge.app.db.domain
	 * .RFIDEvent)
	 */
	@Override
	public void addRow(RFIDEvent event) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put(COL_ID, event.getId());
		parameters.put(COL_READER, event.getReader());
		parameters.put(COL_ANT, event.getAntenna());
		parameters.put(COL_TIME, new Timestamp(event.getTimestamp()));
		insertRFIDEvent.execute(parameters);

	}

	/**
	 * Method called by spring to inject the datasource. This method also opens
	 * up the connection to the datasource.
	 * 
	 * @param ds
	 */
	public void setDataSource(SimpleDriverDataSource ds) {
		this.ds = ds;
		template = new SimpleJdbcTemplate(ds);

		// first thing to do is to drop the table if it exists already.
		try {
			template.update("drop table " + TABLE, new Object[] {});
			logger.debug("RFIDEVENTS table dropped");
		} catch (DataAccessException dae) {
		}

		// next create the table
		template.update("create table " + TABLE + "(" + COL_ID
				+ " VARCHAR(24), " + COL_READER + " VARCHAR(50)," + COL_ANT
				+ " INTEGER, " + COL_TIME + " TIMESTAMP)", new Object[] {});
		logger.debug("New RFIDEVENTS table created");
		insertRFIDEvent = new SimpleJdbcInsert(ds).withTableName(TABLE);
	}

}
