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
 * @author matt
 * 
 */
public class UniqueReadZoneMonitorEsperFactory implements RifidiAppEsperFactory {

	/** The set of read zones to monitor */
	private final List<ReadZone> readzones;
	/** The name of the esper window to use */
	private final String windowName;
	private final String uniqueWindowName;
	/** The list of esper statements */
	private final List<String> statements;
	/** The amount of time to wait before deciding a tag has departed. */
	private final Float departureWaitTime;
	/** The time unit used for the departure wait time */
	private final TimeUnit timeUnit;
	/** The logger for this class */
	private final static Log logger = LogFactory
			.getLog(ReadZoneMonitorEsperFactory.class);

	/**
	 * Constructor for this factory
	 * 
	 * @param readzones
	 *            The set of read zones to monitor
	 * @param windowID
	 *            A unique integer should be given in order to make all window
	 *            names unique
	 * @param departureWaitTime
	 *            The amount of time to wait before deciding a tag has departed.
	 * @param timeUnit
	 *            the timeUnit used for the departure time.
	 * @param uniquereader
	 *            If this value is set to true, a new arrival event will be
	 *            generated for each reader within the given readzones that a
	 *            tag shows up on. For instance, suppose the subscriber is
	 *            monitoring readers LLRP_1 and LLRP_2. If a tag is already
	 *            visible on LLRP_1, and it suddenly becomes visible on LLRP_2,
	 *            a new arrival event will be triggered. If the value is set to
	 *            false, no arrival event will be triggered
	 * @param uniqueantenna
	 *            If this value is set to true, a new unique antenna
	 */
	public UniqueReadZoneMonitorEsperFactory(List<ReadZone> readzones,
			Integer windowID, Float departureWaitTime, TimeUnit timeUnit,
			boolean uniquereader, boolean uniqueantenna) {
		this.readzones = new ArrayList<ReadZone>();
		if (readzones != null) {
			this.readzones.addAll(readzones);
		}
		this.windowName = "tags_" + windowID;
		this.uniqueWindowName = windowName + "_unique";
		statements = new LinkedList<String>();
		this.departureWaitTime = departureWaitTime;
		this.timeUnit = timeUnit;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select irstream * from " + uniqueWindowName;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.service.RifidiAppEsperFactory#createStatements()
	 */
	@Override
	public List<String> createStatements() {
		List<String> statements = new ArrayList<String>();
		String time = EsperUtil.timeUnitToEsperTime(departureWaitTime, timeUnit);
		String windowTime = EsperUtil.timeUnitToEsperTime(0.01f, TimeUnit.SECONDS);
		statements.add("create window " + windowName
				+ ".std:firstunique(tag.ID, readerID"
				+ ", antennaID) as TagReadEvent");
		statements.add(EsperUtil.buildInsertStatement(windowName, readzones));
		statements.add("create window " + uniqueWindowName + ".std:firstunique(tag.ID) as TagReadEvent");
		statements.add("insert into " + uniqueWindowName + " select * from " + windowName);
		statements.add("on pattern [every tag1="+ windowName+ " ->"
				+ "(timer:interval("+ windowTime+ ")and not " + windowName
				+ "(tag.ID=tag1.tag.ID, readerID=tag1.readerID, antennaID=tag1.antennaID))]"
				+ "delete from "+ windowName + " where "
				+ "tag.ID = tag1.tag.ID AND readerID=tag1.readerID AND antennaID=tag1.antennaID");
		statements.add("on pattern [every tag1="+ uniqueWindowName+ " ->"
				+ "(timer:interval("+ time+ ")and not " + uniqueWindowName
				+ "(tag.ID=tag1.tag.ID))]"
				+ "delete from "+ uniqueWindowName + " where "
				+ "tag.ID = tag1.tag.ID");
		return statements;
	}
}
