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
/**
 * 
 */
package org.rifidi.edge.api.service.tagmonitor;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.service.EsperUtil;
import org.rifidi.edge.api.service.RifidiAppEsperFactory;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class RSSIMonitorEsperFactory implements RifidiAppEsperFactory {

	/** The set of read zones to monitor */
	private final List<ReadZone> readzones;
	/** The name of the esper window to use */
	private final String windowName;
	
	private final String windowName_avg;
	
	private final String windowName_first;
	
	private final String windowName_sub;
	/** The list of esper statements */
	private final List<String> statements;
	/** The time unit used for the departure wait time */
	private final TimeUnit timeUnit;
	/** The logger for this class */
	private final static Log logger = LogFactory
			.getLog(RSSIMonitorEsperFactory.class);
	private final String interval;
	
	private final Integer countThreshold;
	
	private final Double minAvgRSSIThreshold;

	public RSSIMonitorEsperFactory(List<ReadZone> readzones, Integer windowID,
			Float windowTime, TimeUnit timeUnit, Integer countThreshold, Double mingAvgRSSIThreshold) {
		this.readzones = new ArrayList<ReadZone>();
		if (readzones != null) {
			this.readzones.addAll(readzones);
		}
		this.windowName = "rssi_" + windowID;
		this.windowName_avg = windowName + "_avg";
		this.windowName_first = windowName + "_first";
		this.windowName_sub = windowName + "_sub";
		//this.slidingWindowName = "sliding_" + windowID;
		statements = new LinkedList<String>();
		//this.windowTime = windowTime;
		this.timeUnit = timeUnit;
		this.interval = EsperUtil.timeUnitToEsperTime(windowTime, timeUnit);
		this.countThreshold = countThreshold;
		this.minAvgRSSIThreshold = mingAvgRSSIThreshold;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createStatements()
	 */
	@Override
	public List<String> createStatements() {
		statements.add("create window " + windowName_first + ".win:time_batch("+interval+") as TagReadEvent");
		statements.add(EsperUtil.buildInsertStatement(windowName_first, readzones, false));
		statements.add("create window " + windowName_avg + ".win:time_batch("+interval+") as RSSITagReadEvent");
		statements.add("insert into "  + windowName_avg + " select tag.formattedID, readerID, avg(cast(extraInformation('RSSI'),Double)) as avgRSSI, cast(count(*),Double) as tagCount from "
					+ windowName_first + " group by tag.ID, readerID having cast(count(*),Double)>" + countThreshold + " and avg(cast(extraInformation('RSSI'),Double))>" + minAvgRSSIThreshold );		
		statements.add("insert into "  + windowName + " select tagID, maxby(avgRSSI).readerID from "
				+ windowName_avg + " group by tagID"  );
		return statements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select irstream * from " + windowName + " output every " + interval;
	}
}
