/*
 *  ToolcribPortalApp.java
 *
 *  Created:	Mar 8, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package com.csc.rfid.toolcrib;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.management.SessionDownEvent;
import org.rifidi.edge.core.services.notification.data.management.SessionUpEvent;

import com.csc.rfid.toolcrib.utilities.DirectionAlgorithm;
import com.csc.rfid.toolcrib.utilities.RifidiLogEntryCreationUtility;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is a part of the Toolcrib application designed specifically for the
 * portal. It will handle the ESPER statements needed to get the required
 * information and the logic needed to calculate the directionality of the tag.
 * 
 * @author Matthew Dean
 */
public class ToolcribPortalApp extends ToolcribApp {

	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();

	private static final Log logger = LogFactory.getLog(ToolcribApp.class);
	private final DirectionAlgorithm algorithm = new DirectionAlgorithm();

	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime.
	 * 
	 * This method is called by spring
	 */
	public void start() {
		logger.debug("Starting CSC App");

		// esper statement that creates a window.
		statements.add(esperService.getProvider().getEPAdministrator()
				.createEPL(
						"create window tagwin.win:keepall()"
								+ "(tag_ID String, speed Float, rssi "
								+ "String, readerID String, antennaID int)"));

		// esper statement taht adds information to the window
		// TODO make sure that velocity info is inserted into the window/
		statements
				.add(esperService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"on ReadCycle[select * from tags]"
										+ "insert into tagwin select cast(tag.epc?, String) as tag_ID , "
										+ "cast(extraInformation('Speed'),Float) as speed , "
										+ "cast(extraInformation('RSSI'),String) as rssi, "
										+ "readerID, antennaID"));

		// esper statement that removes all tags with a given ID from the window
		// if the tag has not been seen at the antenna in the last 60 seconds
		statements
				.add(esperService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"on pattern [every tag=tagwin ->"
										+ "(timer:interval("
										+ System
												.getProperty("com.csc.tagreadcutofftime")
										+ " sec) and not tagwin"
										+ "(tag_ID = tag.tag_ID))]"
										+ "delete from tagwin where "
										+ "tag_ID = tag.tag_ID"));

		// esper statement that listens to add and remove events from the window
		EPStatement queryAllTags = esperService.getProvider()
				.getEPAdministrator()
				.createEPL("select irstream * from tagwin");

		// add a listener to the above statement
		queryAllTags.addListener(getTagsUpdateListener());

		EPStatement queryDownTimeEvent = esperService.getProvider()
				.getEPAdministrator().createEPL(
						"select * from SessionDownEvent");
		statements.add(queryDownTimeEvent);

		EPStatement queryUpTimeEvent = esperService.getProvider()
				.getEPAdministrator().createEPL("select * from SessionUpEvent");
		statements.add(queryUpTimeEvent);

		StatementAwareUpdateListener stateUpdateListener = getSessionStateUpdateListener();
		queryDownTimeEvent.addListener(stateUpdateListener);
		queryUpTimeEvent.addListener(stateUpdateListener);

		statements.add(queryAllTags);
	}

	public StatementAwareUpdateListener getTagsUpdateListener() {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {

				// Map that will hold the tag ID bound to all the speed
				// information that is retrieved.
				// HashMap<String, List<CSCTag>> speedMap = new HashMap<String,
				// List<CSCTag>>();
				List<CSCTag> tags = new LinkedList<CSCTag>();

				// arg1 contains all the tag reads. This works because we do all
				// of the rmoves at once.
				if (arg1 != null) {
					for (EventBean b : arg1) {
						CSCTag cscTag = new CSCTag();
						cscTag.setEpc((String) b.get("tag_ID"));
						cscTag.setSpeed((Float) b.get("speed"));
						cscTag.setRssi((String) b.get("rssi"));
						cscTag.setReaderID((String) b.get("readerID"));
						cscTag.setAntenna((Integer) b.get("antennaID"));
						tags.add(cscTag);
						logger.debug(cscTag);
					}

					if (isGhost(tags)) {
						handleGhost(tags);
						return;
					}

					float speed = calculateDirection(tags);
					boolean onWatchList = false;

					boolean inbound = false;
					if (speed > 0.0f) {
						inbound = true;
					} else {
						inbound = false;
					}

					// Flips the speed if this particular property is set.
					inbound = check_orientations(tags.get(0).getReaderID(),
							inbound);

					logger.debug("Tag is: " + tags.get(0).getEpc()
							+ ", Speed is: " + speed + ", Direction is: "
							+ (inbound ? "Inbound" : "OutBound"));

					if (onWatchlist(tags)) {
						onWatchList = true;
					}

					System.out.println("Inbound: " + inbound);

					triggerLight(tags.get(0).getReaderID(), onWatchList,
							inbound);
					writeLog(tags.get(0).getEpc(), tags.get(0).getReaderID(),
							inbound, onWatchList);

				}
			}
		};
	}

	public StatementAwareUpdateListener getSessionStateUpdateListener() {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean b : arg0) {
						if (b.getUnderlying() instanceof SessionUpEvent) {
							logger.debug(b.getUnderlying());
							SessionUpEvent sue = (SessionUpEvent) b
									.getUnderlying();
							logFile
									.writeDowntimeLog(RifidiLogEntryCreationUtility
											.createUptimeLogEntry(sue
													.getReaderID(), sue
													.getTimestamp()));
						} else if (b.getUnderlying() instanceof SessionDownEvent) {
							logger.debug(b.getUnderlying());
							SessionDownEvent sue = (SessionDownEvent) b
									.getUnderlying();
							logFile
									.writeDowntimeLog(RifidiLogEntryCreationUtility
											.createDowntimeLogEntry(sue
													.getReaderID(), sue
													.getTimestamp()));
						}
					}
				}

			}
		};
	}

	/**
	 * >0 is incoming 0 is not sure <0 is outgoing
	 * 
	 * @param tags
	 * @return
	 */
	private Float calculateDirection(List<CSCTag> tags) {
		List<Float> speeds = new ArrayList<Float>();
		for (CSCTag tag : tags) {
			speeds.add(tag.getSpeed());
		}
		return algorithm.getSpeed(speeds);
	}

	/**
	 * Called by spring Iterate through all statements and stop them.
	 */
	public void stop() {
		for (EPStatement statement : statements) {
			statement.destroy();
		}
	}
}
