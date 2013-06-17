/**
 * 
 */
package org.rifidi.app.db;

import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * The subscriber class. This class monitors the arrived and departed events for
 * all readers.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class DBSubscriber implements ReadZoneSubscriber {

	private DatabaseConnection conn;

	/**
	 * Constructor
	 * 
	 * @param conn
	 *            The database connection
	 */
	public DBSubscriber(DatabaseConnection conn) {
		this.conn = conn;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber#tagArrived(
	 * org.rifidi.edge.notification.TagReadEvent)
	 */
	@Override
	public void tagArrived(TagReadEvent tag) {
		// write the tag to the database
		this.conn.arrivedEvent(tag);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber#tagDeparted
	 * (org.rifidi.edge.notification.TagReadEvent)
	 */
	@Override
	public void tagDeparted(TagReadEvent tag) {
		this.conn.departedEvent(tag);
	}
}
