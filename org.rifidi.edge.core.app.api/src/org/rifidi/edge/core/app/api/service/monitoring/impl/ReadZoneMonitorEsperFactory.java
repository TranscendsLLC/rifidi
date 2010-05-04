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
package org.rifidi.edge.core.app.api.service.monitoring.impl;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.core.app.api.service.monitoring.ReadZone;

/**
 * This is a factory that creates the esper statements for monitoring read
 * zones. A new factory should be created for every new set of readers.
 * 
 * @author Matthew Dean
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ReadZoneMonitorEsperFactory {

	/** The set of read zones to monitor */
	private final Set<ReadZone> readzones;
	/** The name of the esper window to use */
	private final String windowName;
	/** The list of espoer statements */
	private final List<String> statements;
	/** The amount of time to wait before deciding a tag has departed. */
	private final Integer departureWaitTime;

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
		this.readzones = new HashSet<ReadZone>();
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
		return "insert into " + windowName
				+ " select * from ReadCycle[select * from tags]"
				+ whereClause();
	}

	/**
	 * A private method to create the where clause in the select statement
	 * 
	 * @return
	 */
	private String whereClause() {
		if (readzones.isEmpty()) {
			return "";
		} else {
			// TODO: implement this
			return "";
		}
	}

	/**
	 * A private method to create the delete statement
	 * 
	 * @return
	 */
	private String deleteStatement() {
		return "on pattern " +
		"[every tag1=" +windowName + " ->"+ 
		"(timer:interval("+ Integer.toString(departureWaitTime)	+ " sec) " +
		"and not "+ windowName	+
			"(tag.ID=tag1.tag.ID, readerID=tag1.readerID, antennaID=tag1.antennaID))]" +
			"delete from "+ windowName+ " where "	+ 
			"tag.ID = tag1.tag.ID AND readerID=tag1.readerID AND antennaID=tag1.antennaID";
	}

}
