/**
 * 
 */
package org.rifidi.edge.app.db.dao;

import org.rifidi.edge.app.db.domain.RFIDEvent;

/**
 * This interface is implemented by Data Access Objects (DAO) that have the
 * ability to write to our data source.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RFID_DAO {

	/***
	 * Write the supplied event to the data source
	 * 
	 * @param event
	 */
	public void addRow(RFIDEvent event);

}
