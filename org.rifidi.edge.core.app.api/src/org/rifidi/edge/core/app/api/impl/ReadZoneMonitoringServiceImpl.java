/*
 *  ReadZoneMonitoringServiceImpl.java
 *
 *  Created:	Mar 16, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.core.app.api.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rifidi.edge.core.app.api.ReadZoneMonitoringService;
import org.rifidi.edge.core.app.api.ReadZoneSubscriber;
import org.rifidi.edge.core.app.api.events.ArriveEvent;
import org.rifidi.edge.core.app.api.events.DepartEvent;
import org.rifidi.edge.core.app.api.events.RFIDEvent;
import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ReadZoneMonitoringServiceImpl implements ReadZoneMonitoringService {

	private Map<ReadZoneSubscriber, Set<String>> subscriberMap = new HashMap<ReadZoneSubscriber, Set<String>>();

	/** Esper service */
	private volatile EsperManagementService esperService;
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();

	/**
	 * 
	 */
	public void start() {
		// esper statement that creates a window. There are three pieces of
		// information in each column: tagID, readerID, and antennaID
		statements.add(esperService.getProvider().getEPAdministrator()
				.createEPL(
						"create window tags.std:firstunique("
								+ "tag_ID, reader_ID, antenna) "
								+ "(reader_ID String, antenna "
								+ "int, tag_ID String)"));

		// esper statement taht adds information to the window
		statements.add(esperService.getProvider().getEPAdministrator()
				.createEPL(
						"on ReadCycle[select * from tags] "
								+ "insert into tags "
								+ "select readerID as reader_ID, "
								+ "antennaID as antenna, "
								+ "cast(tag.epc?, String) as tag_ID"));

		// esper statement that removes rows from the window if the tag has not
		// been seen at
		// the antenna in the last 2 seconds
		statements
				.add(esperService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"on pattern [every tag=tags ->"
										+ "(timer:interval(2 sec) and not tags"
										+ "(tag_ID = tag.tag_ID, reader_ID=tag.reader_ID, "
										+ "antenna=tag.antenna))]"
										+ "delete from tags where "
										+ "tag_ID = tag.tag_ID AND reader_ID=tag.reader_ID "
										+ "AND antenna=tag.antenna"));

		// esper statement that listens to add and remove events from the window
		EPStatement queryAllTags = esperService.getProvider()
				.getEPAdministrator().createEPL("select irstream * from tags");

		// add a listener to the above statement
		queryAllTags.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {

				// all additions
				if (arg0 != null && arg0.length != 0) {
					List<RFIDEvent> eventList = new LinkedList<RFIDEvent>();
					String readerID = "";
					for (EventBean b : arg0) {
						ArriveEvent ae = new ArriveEvent();
						String reader = (String) b.get("reader_ID");
						Integer antennaID = (Integer) b.get("antenna");
						String epc = (String) b.get("tag_ID");
						ae.setEpc(epc);
						ae.setAntenna(antennaID);
						ae.setReaderID(reader);
						eventList.add(ae);
					}
					readerID = (String) eventList.get(0).getReaderID();
					for (ReadZoneSubscriber rzs : subscriberMap.keySet()) {
						for (String reader : subscriberMap.get(rzs)) {
							if (readerID.equals(reader)) {
								for (RFIDEvent re : eventList) {
									rzs.handleEvent(re);
								}
							}
						}
					}
				}
				// all deletions
				if (arg1 != null && arg1.length != 0) {
					List<RFIDEvent> eventList = new LinkedList<RFIDEvent>();
					String readerID = "";
					for (EventBean b : arg1) {
						DepartEvent de = new DepartEvent();
						String reader = (String) b.get("reader_ID");
						Integer antennaID = (Integer) b.get("antenna");
						String epc = (String) b.get("tag_ID");
						de.setEpc(epc);
						de.setAntenna(antennaID);
						de.setReaderID(reader);
						eventList.add(de);
					}
					readerID = (String) eventList.get(0).getReaderID();
					for (ReadZoneSubscriber rzs : subscriberMap.keySet()) {
						for (String reader : subscriberMap.get(rzs)) {
							if (readerID.equals(reader)) {
								for (RFIDEvent re : eventList) {
									rzs.handleEvent(re);
								}
							}
						}
					}
				}

			}
		});
		statements.add(queryAllTags);
	}

	/**
	 * 
	 */
	public void stop() {
		for (EPStatement statement : statements) {
			statement.destroy();
		}
	}

	/**
	 * Called by spring
	 * 
	 * @param esperService
	 *            the esperService to set
	 */
	public void setEsperService(EsperManagementService esperService) {
		this.esperService = esperService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.ReadZoneMonitoringService#subscribe(org.
	 * rifidi.edge.core.app.api.ReadZoneSubscriber, java.util.Set)
	 */
	@Override
	public boolean subscribe(ReadZoneSubscriber rzs,
			Set<String> readerIDsToSubscribeTo) {
		this.subscriberMap.put(rzs, readerIDsToSubscribeTo);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.app.api.ReadZoneMonitoringService#unsubscribe(org
	 * .rifidi.edge.core.app.api.ReadZoneSubscriber)
	 */
	@Override
	public boolean unsubscribe(ReadZoneSubscriber rzs) {
		this.subscriberMap.remove(rzs);
		return true;
	}

}
