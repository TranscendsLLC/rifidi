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
package org.rifidi.edge.api.service.sensormonitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.rifidi.edge.api.service.RifidiAppEsperFactory;


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
	 * org.rifidi.edge.api.service.RifidiAppEsperFactory#createQuery()
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
	 * org.rifidi.edge.api.service.RifidiAppEsperFactory#createStatements
	 * ()
	 */
	@Override
	public List<String> createStatements() {
		return Collections.emptyList();
	}

}
