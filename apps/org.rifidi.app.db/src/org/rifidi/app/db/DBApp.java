/**
 * 
 */
package org.rifidi.app.db;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.ReadZoneSubscriber;

/**
 * @author Matthew Dean - matt@transcends.co
 * 
 */
public class DBApp extends AbstractRifidiApp {

	// Property names
	public static final String TIMEOUT = "Timeout";

	private DatabaseConnection conn;

	/** The service for monitoring arrival and departure events */
	private ReadZoneMonitoringService readZoneMonitoringService;

	private float timeout;
	
	private List<ReadZoneSubscriber> subscriberList;

	public DBApp(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_start()
	 */
	@Override
	public void _start() {
		super._start();
		this.conn.init();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_stop()
	 */
	@Override
	public void _stop() {
		for (ReadZoneSubscriber s : this.subscriberList) {
			this.readZoneMonitoringService.unsubscribe(s);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		this.timeout = Float.parseFloat(this.getProperty(TIMEOUT, "8.0"));
		DBSubscriber sub = new DBSubscriber(this.conn);
		this.subscriberList = new LinkedList<ReadZoneSubscriber>();
		this.subscriberList.add(sub);
		this.readZoneMonitoringService.subscribe(sub,
				new LinkedList<ReadZone>(), this.timeout, TimeUnit.SECONDS, true);
	}

	/**
	 * Called by spring.
	 * 
	 * @param conn
	 */
	public void setDatabaseConnection(DatabaseConnection conn) {
		this.conn = conn;
	}

	/**
	 * Called by spring. This method injects the ReadZoneMonitoringService into
	 * the application.
	 * 
	 * @param rzms
	 */
	public void setReadZoneMonitoringService(ReadZoneMonitoringService rzms) {
		this.readZoneMonitoringService = rzms;
	}

}
