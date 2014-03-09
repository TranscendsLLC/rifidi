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
		statements.add(EsperUtil.buildInsertStatement(windowName_first, readzones));
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
