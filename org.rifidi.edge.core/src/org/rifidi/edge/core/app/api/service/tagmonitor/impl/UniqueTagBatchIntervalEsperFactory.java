/**
 * 
 */
package org.rifidi.edge.core.app.api.service.tagmonitor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.core.app.api.service.EsperUtil;
import org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;

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
	 * org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory#createQuery()
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
	 * org.rifidi.edge.core.app.api.service.RifidiAppEsperFactory#createStatements
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
