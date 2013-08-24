/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
		statements.add(EsperUtil.buildInsertStatement(windowName, readZones));
		statements.add("on pattern[every timer:interval(" + interval
				+ ")] delete from " + windowName);
		return statements;
	}

}
