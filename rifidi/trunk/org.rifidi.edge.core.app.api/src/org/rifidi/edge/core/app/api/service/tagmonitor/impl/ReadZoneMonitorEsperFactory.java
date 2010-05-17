/*
 *  ReadZoneEsperConstructor.java
 *
 *  Created:	Mar 17, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.core.app.api.service.tagmonitor.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;

/**
 * This is a factory that creates the esper statements for monitoring read
 * zones. A new factory should be created for every new set of readers.
 * 
 * @author Matthew Dean
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReadZoneMonitorEsperFactory {

	/** The set of read zones to monitor */
	private final List<ReadZone> readzones;
	/** The name of the esper window to use */
	private final String windowName;
	/** The list of espoer statements */
	private final List<String> statements;
	/** The amount of time to wait before deciding a tag has departed. */
	private final Integer departureWaitTime;
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
	 *            The amount of time ins seconds to wait before deciding a tag
	 *            has departed.
	 */
	public ReadZoneMonitorEsperFactory(Set<ReadZone> readzones,
			Integer windowID, Integer departureWaitTime) {
		this.readzones = new ArrayList<ReadZone>();
		if (readzones != null) {
			this.readzones.addAll(readzones);
		}
		this.windowName = "tags_" + windowID;
		statements = new LinkedList<String>();
		this.departureWaitTime = departureWaitTime;

	}

	/**
	 * Use this method to create the necessary statements
	 * 
	 * @return A list of esper statements as strings
	 */
	public List<String> createStatements() {
		statements.add(createWindowStatement());
		statements.add(insertStatement());
		statements.add(deleteStatement());
		return statements;
	}

	/**
	 * Use this method to create a query that monitors the window
	 * 
	 * @return
	 */
	public String getQuery() {
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
	 * A private method to create the "insert into" statement
	 * 
	 * @return
	 */
	private String insertStatement() {
		String s = "insert into " + windowName
				+ " select * from ReadCycle[select * from tags "
				+ whereClause() + "]";
		logger.debug("Insert statement: " + s);
		return s;
	}

	/**
	 * A private method to create the where clause in the select statement
	 * 
	 * @return
	 */
	private String whereClause() {
		StringBuilder builder = new StringBuilder();
		if (!readzones.isEmpty()) {
			builder.append(" where (" + buildReader(readzones.get(0)));
			builder.append("");
			for (int i = 1; i < readzones.size(); i++) {
				builder.append(" OR " + buildReader(readzones.get(i)));
			}
			builder.append(")");
		}
		return builder.toString();
	}

	/**
	 * This method builds a single reader filter. For example:
	 * 
	 * (TagReadEvent.readerID='Alien_1'
	 * 
	 * or
	 * 
	 * (tagReadEvent.readerID='Alien_1' AND (antennaID=2))
	 * 
	 * 
	 * @param zone
	 * @return
	 */
	private String buildReader(ReadZone zone) {
		StringBuilder sb = new StringBuilder("(TagReadEvent.readerID=\'"
				+ zone.getReaderID() + "\'");
		sb.append(buildAntenns(new ArrayList<Integer>(zone.getAntennas())));
		sb.append(")");
		return sb.toString();
	}

	/**
	 * This method builds a list of antennaID filters. For example:
	 * 
	 * (antennaID=1)
	 * 
	 * or
	 * 
	 * (antennaID=1 OR antennaID=2)
	 * 
	 * @param antennas
	 * @return
	 */
	private String buildAntenns(List<Integer> antennas) {
		StringBuilder sb = new StringBuilder();
		if (antennas.size() > 0) {
			sb.append(" AND (" + buildAntenna(antennas.get(0)));
			for (int i = 1; i < antennas.size(); i++) {
				sb.append(" OR " + buildAntenna(antennas.get(i)));
			}
			sb.append(")");

		}
		return sb.toString();
	}

	/**
	 * This method returns a single antennaID filter.
	 * 
	 * @param antenna
	 * @return
	 */
	private String buildAntenna(Integer antenna) {
		return "antennaID=" + antenna;
	}

	/**
	 * A private method to create the delete statement
	 * 
	 * @return
	 */
	private String deleteStatement() {
		return "on pattern "
				+ "[every tag1="
				+ windowName
				+ " ->"
				+ "(timer:interval("
				+ Integer.toString(departureWaitTime)
				+ " sec) "
				+ "and not "
				+ windowName
				+ "(tag.ID=tag1.tag.ID, readerID=tag1.readerID, antennaID=tag1.antennaID))]"
				+ "delete from "
				+ windowName
				+ " where "
				+ "tag.ID = tag1.tag.ID AND readerID=tag1.readerID AND antennaID=tag1.antennaID";
	}

}
