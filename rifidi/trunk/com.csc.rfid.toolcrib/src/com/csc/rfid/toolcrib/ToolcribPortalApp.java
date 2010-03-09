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
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

import com.csc.rfid.toolcrib.utilities.DirectionAlgorithm;
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

	private static final Log logger = LogFactory
			.getLog(ToolcribPortalApp.class);
	private final DirectionAlgorithm algorithm = new DirectionAlgorithm();

	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime.
	 * 
	 * This method is called by spring
	 */
	public void start() {
		logger.debug("Starting CSC Portal App");
		String portalReaderID = System.getProperty("com.csc.portal_reader");
		String cutOffTime = System.getProperty("com.csc.tagreadcutofftime");
		System.out.println("readerID: " + portalReaderID);
		EPStatement query = esperService.getProvider().getEPAdministrator()
				.createEPL(
						"select * from pattern [every gpievent=GPIEvent(state=false, readerID='"
								+ portalReaderID + "')->"
								+ "tags=ReadCycle(readerID='" + portalReaderID
								+ "')[select * from tags] "
								+ "until timer:interval(" + cutOffTime
								+ " sec)]");
		statements.add(query);
		query.addListener(getTagsUpdateListener());

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
				if (arg0 != null) {
					TagReadEvent[] tagReadEvents = null;
					GPIEvent gpiEvent = null;
					for (EventBean b : arg0) {
						tagReadEvents = (TagReadEvent[]) b.get("tags");
						gpiEvent = (GPIEvent) b.get("gpievent");
					}
					if (tagReadEvents == null || gpiEvent == null) {
						return;
					}

					for (TagReadEvent tre : tagReadEvents) {
						CSCTag tag = new CSCTag();
						tag.setAntenna(tre.getAntennaID());
						tag.setEpc(((EPCGeneration2Event) tre.getTag())
								.getEpc());
						tag.setReaderID(tre.getReaderID());
						tag.setRssi((String) tre.getExtraInformation().get(
								"RSSI"));
						tag.setSpeed((Float) tre.getExtraInformation().get(
								"Speed"));
						tags.add(tag);
					}

					if (isGhost(tags)) {
						handleGhost(tags);
						return;
					}

					boolean onWatchList = false;

					boolean inbound = false;
					if (gpiEvent.getPort() == 1) {
						inbound = true;
					}

					// Flips the speed if this particular property is set.
					inbound = check_orientations(tags.get(0).getReaderID(),
							inbound);

					logger.info("Tag is: " + tags.get(0).getEpc()
							+ ", Direction is: "
							+ (inbound ? "Inbound" : "Outbound"));

					if (onWatchlist(tags)) {
						onWatchList = true;
					}

					triggerLight(tags.get(0).getReaderID(), onWatchList,
							inbound);
					writeLog(tags.get(0).getEpc(), tags.get(0).getReaderID(),
							inbound, onWatchList);

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
