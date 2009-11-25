/**
 * 
 */
package org.rifidi.edge.app.db.dao;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.app.db.domain.RFIDEvent;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

/**
 * @author kyle
 * 
 */
public class RFIDDAOImpl implements RFIDDAO {

	private SimpleJdbcTemplate template;
	private SimpleJdbcInsert insertRFIDEvent;
	private SimpleDriverDataSource ds;
	private Log logger = LogFactory.getLog(RFIDDAOImpl.class);
	private static final String TABLE = "rfidevents";
	private static final String COL_ID = "id";
	private static final String COL_READER = "reader";
	private static final String COL_ANT = "antenna";
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
		Map<String,Object> parameters = new HashMap<String, Object>();
		parameters.put(COL_ID, event.getId());
		parameters.put(COL_READER, event.getReader());
		parameters.put(COL_ANT, event.getAntenna());
		parameters.put(COL_TIME, new Timestamp(event.getTimestamp()));
		insertRFIDEvent.execute(parameters);

	}

	public void setDataSource(SimpleDriverDataSource ds) {
		this.ds = ds;
		template = new SimpleJdbcTemplate(ds);
		try {
			template.update("drop table " + TABLE, new Object[]{});
		} catch (DataAccessException dae) {
		}
		template.update("create table " + TABLE +"(" +
				COL_ID + " VARCHAR(24), " +
				COL_READER + " VARCHAR(50)," +
				COL_ANT + " INTEGER, " +
				COL_TIME + " TIMESTAMP)",
				new Object[] {});
		logger.debug("New RFIDEVENTS table created");
		insertRFIDEvent = new SimpleJdbcInsert(ds).withTableName(TABLE);
	}
	
}
