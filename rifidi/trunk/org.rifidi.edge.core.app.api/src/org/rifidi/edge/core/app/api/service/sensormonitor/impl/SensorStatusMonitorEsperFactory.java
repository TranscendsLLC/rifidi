/**
 * 
 */
package org.rifidi.edge.core.app.api.service.sensormonitor.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory;

/**
 * An Esper factory that creates the esper statements to monitor the status
 * events of Sensors
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SensorStatusMonitorEsperFactory implements RifidiAppEsperFactory {

	/** The Sensors to monitor */
	private final ArrayList<String> readersToMonitor;

	/**
	 * Constructor
	 * 
	 * @param readersToMonitor
	 *            A list of sensorIDs to monitor
	 */
	public SensorStatusMonitorEsperFactory(ArrayList<String> readersToMonitor) {
		super();
		this.readersToMonitor = readersToMonitor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		String statement = "select * from SensorStatusEvent";
		// build where clause
		if (!readersToMonitor.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			sb.append(" where " + sensorFilter(readersToMonitor.get(0)));
			for (int i = 1; i < readersToMonitor.size(); i++) {
				sb.append(" or " + sensorFilter(readersToMonitor.get(i)));
			}
			statement = statement + sb.toString();
		}
		return statement;
	}

	/**
	 * A private method to build a filter for a single reader.
	 * 
	 * @param sensor
	 * @return
	 */
	private String sensorFilter(String sensor) {
		return "sensorID=\'" + sensor + "\'";
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory#createStatements
	 * ()
	 */
	@Override
	public List<String> createStatements() {
		return Collections.emptyList();
	}

}
