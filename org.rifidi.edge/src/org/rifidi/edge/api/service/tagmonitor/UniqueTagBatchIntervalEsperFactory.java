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
package org.rifidi.edge.api.service.tagmonitor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.api.service.EsperUtil;
import org.rifidi.edge.api.service.RifidiAppEsperFactory;

/**
 * The factory used to create the esper statements for the
 * UniqueTagBatchIntervalService
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class UniqueTagBatchIntervalEsperFactory implements
		RifidiAppEsperFactory {

	/** The read zones to monitor */
	private List<ReadZone> readZones;
	/** The interval at which to report tag batches */
	private String interval;
	/** The window to monitor */
	private String windowName;

	/**
	 * Constructor
	 * 
	 * @param zones
	 *            The read zones to monitor
	 * @param time
	 *            The interval to report tag batches back
	 * @param timeUnit
	 *            The time unit of the interval
	 * @param windowID
	 *            A unique number used to create a unique window
	 */
	public UniqueTagBatchIntervalEsperFactory(List<ReadZone> zones, float time,
			TimeUnit timeUnit, int windowID) {
		interval = EsperUtil.timeUnitToEsperTime(time, timeUnit);
		windowName = "rifidi_appservice_UniqueTagBatchInterval_" + windowID;
		this.readZones = zones;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select irstream * from " + windowName + " output every "
				+ interval;
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
		List<String> statements = new ArrayList<String>();
		statements.add("create window " + windowName
				+ ".std:firstunique(tag.ID) as TagReadEvent");
		statements.add(EsperUtil.buildInsertStatement(windowName, readZones, false));
		statements.add("on pattern[every timer:interval(" + interval
				+ ")] delete from " + windowName);
		return statements;
	}

}
