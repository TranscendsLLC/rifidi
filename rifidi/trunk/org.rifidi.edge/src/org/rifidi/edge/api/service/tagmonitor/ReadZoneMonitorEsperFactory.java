/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
 * This is a factory that creates the esper statements for monitoring read
 * zones. A new factory should be created for every new set of readers.
 * 
 * @author Matthew Dean
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReadZoneMonitorEsperFactory implements RifidiAppEsperFactory{

	/** The set of read zones to monitor */
	private final List<ReadZone> readzones;
	/** The name of the esper window to use */
	private final String windowName;
	/** The list of espoer statements */
	private final List<String> statements;
	/** The amount of time to wait before deciding a tag has departed. */
	private final Float departureWaitTime;
	/** The time unit used for the departure wait time */
	private final TimeUnit timeUnit;
	/** Is the readername a regex? */
	private final Boolean useRegex;
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
	 */
	public ReadZoneMonitorEsperFactory(List<ReadZone> readzones,
			Integer windowID, Float departureWaitTime, TimeUnit timeUnit, Boolean useRegex) {
		this.readzones = new ArrayList<ReadZone>();
		if (readzones != null) {
			this.readzones.addAll(readzones);
		}
		this.windowName = "tags_" + windowID;
		statements = new LinkedList<String>();
		this.departureWaitTime = departureWaitTime;
		this.timeUnit = timeUnit;
		this.useRegex = useRegex;
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
		statements.add(createWindowStatement());
		String insertStatement = EsperUtil.buildInsertStatement(windowName, readzones, useRegex);
		statements.add(insertStatement);
		statements.add(deleteStatement());
		return statements;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.api.service.RifidiAppEsperFactory#createQuery()
	 */
	@Override
	public String createQuery() {
		return "select irstream * from " + windowName;
	}

	/**
	 * A private method to create the "create window" statement
	 * 
	 * @return
	 */
	private String createWindowStatement() {
		return "create window "
				+ windowName
				+ ".std:firstunique(tag.ID, readerID, antennaID) as TagReadEvent";
	}

	/**
	 * A private method to create the delete statement
	 * 
	 * @return
	 */
	private String deleteStatement() {
		String time = EsperUtil.timeUnitToEsperTime(departureWaitTime, timeUnit);
		return "on pattern [every tag1="+ windowName+ " ->"
				+ "(timer:interval("+ time+ ")and not "+ windowName
				+ "(tag.ID=tag1.tag.ID, readerID=tag1.readerID, antennaID=tag1.antennaID))]"
				+ "delete from "+ windowName + " where "
				+ "tag.ID = tag1.tag.ID AND readerID=tag1.readerID AND antennaID=tag1.antennaID";
	}

}
