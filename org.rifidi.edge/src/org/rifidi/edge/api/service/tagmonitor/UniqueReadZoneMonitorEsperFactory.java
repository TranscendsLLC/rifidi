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
	private boolean useregex;
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
	 * @param useregex
	 * 			  Use a regular expression for the readzone name
	 */
	public UniqueReadZoneMonitorEsperFactory(List<ReadZone> readzones,
			Integer windowID, Float departureWaitTime, TimeUnit timeUnit,
			boolean useregex) {
		this.readzones = new ArrayList<ReadZone>();
		if (readzones != null) {
			this.readzones.addAll(readzones);
		}
		this.windowName = "tags_" + windowID;
		this.uniqueWindowName = windowName + "_unique";
		statements = new LinkedList<String>();
		this.departureWaitTime = departureWaitTime;
		this.timeUnit = timeUnit;
		this.useregex = useregex;
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
		statements.add(EsperUtil.buildInsertStatement(windowName, readzones, useregex));
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
