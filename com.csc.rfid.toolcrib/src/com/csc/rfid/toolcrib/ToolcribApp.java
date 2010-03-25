/*
 *  ToolcribApp.java
 *
 *  Created:	Feb 4, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package com.csc.rfid.toolcrib;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.management.AbstractGPIOService;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

import com.csc.rfid.toolcrib.utilities.CSCLogger;
import com.csc.rfid.toolcrib.utilities.RifidiLogEntryCreationUtility;
import com.csc.rfid.toolcrib.utilities.WatchlistReader;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is a base class for applications that will monitor the incoming and
 * outgoing parts through a window for CSC/UTC. It will be extended by the
 * modules for the window, door, and portal readers.
 * 
 * @author Matthew Dean - matt@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class ToolcribApp {

	/** Esper service */
	protected volatile EsperManagementService esperService;
	/** All statements that have been defined so far */
	protected final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/** This logger records directionality events only */
	protected CSCLogger logFile;
	/** The object that writes to the watchlist file */
	private final WatchlistReader watchlist_reader = new WatchlistReader(System
			.getProperty("com.csc.watchlist"));
	private static final Log logger = LogFactory.getLog(ToolcribApp.class);
	/** Controller for GPO of Alien */
	protected AbstractGPIOService<?> gpoController;

	// GPIO values
	protected static final Integer FLASH_TIME = Integer.parseInt(System
			.getProperty("com.csc.flashtime"));
	protected static final Integer RED_LIGHT_GPO = Integer.parseInt(System
			.getProperty("com.csc.redlightgpo"));
	protected static final Integer YELLOW_LIGHT_GPO = Integer.parseInt(System
			.getProperty("com.csc.yellowlightgpo"));
	protected static final Integer GREEN_LIGHT_GPO = Integer.parseInt(System
			.getProperty("com.csc.greenlightgpo"));

	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime. Implementations should make sure to add their esper
	 * statements to the 'statements' set.
	 * 
	 * This method is called by spring
	 */
	public abstract void start();

	/**
	 * Called by spring Iterate through all statements and stop them.
	 */
	public void stop() {
		for (EPStatement statement : statements) {
			statement.destroy();
		}
	}

	/**
	 * Creates a new Listener that Converts TagReadEvent objects int CSCTag
	 * objects and processes them.
	 * 
	 * @return
	 */
	protected StatementAwareUpdateListener getTagsUpdateListener() {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {

				// the CSCTags that were seen
				List<CSCTag> tags = new LinkedList<CSCTag>();

				// if there are any incoming events
				if (arg0 != null) {
					// the tag reads that were seen
					TagReadEvent[] tagReadEvents = null;
					// the GPI event that triggered the read
					GPIEvent gpiEvent = null;
					for (EventBean b : arg0) {
						tagReadEvents = (TagReadEvent[]) b.get("tags");
						gpiEvent = (GPIEvent) b.get("gpievent");
					}
					// if an event is missing return.
					if (tagReadEvents == null || gpiEvent == null) {
						return;
					}

					// convert TagReadEvents into CSCTags.
					for (TagReadEvent tre : tagReadEvents) {
						CSCTag tag = new CSCTag();
						tag.setAntenna(tre.getAntennaID());
						tag.setEpc(((EPCGeneration2Event) tre.getTag())
								.getEpc());
						tag.setReaderID(tre.getReaderID());
						tag.setRssi(((Float) tre.getExtraInformation().get(
								"RSSI")).toString());
						tag.setSpeed((Float) tre.getExtraInformation().get(
								"Speed"));
						tags.add(tag);
					}

					// If we have a ghost read, then handle it and exit
					if (isGhost(tags)) {
						handleGhost(tags);
						return;
					}

					// flag that marks if this tag is on the watch list
					boolean onWatchList = false;
					// the direction of the event
					boolean inbound = determineDirection(gpiEvent);
					// Flips the speed if this particular property is set.
					inbound = check_orientations(tags.get(0).getReaderID(),
							inbound);

					logger.info("Tag is: " + tags.get(0).getEpc()
							+ ", Direction is: "
							+ (inbound ? "Inbound" : "Outbound"));

					if (onWatchlist(tags)) {
						onWatchList = true;
					}

					// Set the GPIO lights according to the data
					triggerLight(tags.get(0).getReaderID(), onWatchList,
							inbound);
					// write the log file according to the data
					writeLog(tags.get(0).getEpc(), tags.get(0).getReaderID(),
							inbound, onWatchList);
				}
			}
		};
	}

	/**
	 * Return true if inbound, false if outbound
	 * 
	 * @return
	 */
	protected abstract boolean determineDirection(GPIEvent gpievent);

	/*
	 * Checks if the direction needs to be flipped for the given reader ID. If
	 * it does, the return value will be the opposite of the "inbound" value. If
	 * it isn't, the inbound return value will simply be returned.
	 */
	protected boolean check_orientations(String readerID, boolean inbound) {
		if (readerID.equalsIgnoreCase(System
				.getProperty("com.csc.window_reader"))) {
			if (!System.getProperty("com.csc.window_direction_flip")
					.equalsIgnoreCase("0")) {
				return !inbound;
			}
		} else if (readerID.equalsIgnoreCase(System
				.getProperty("com.csc.door_reader"))) {
			if (!System.getProperty("com.csc.door_direction_flip")
					.equalsIgnoreCase("0")) {
				return !inbound;
			}
		} else if (readerID.equalsIgnoreCase(System
				.getProperty("com.csc.portal_reader"))) {
			if (!System.getProperty("com.csc.portal_direction_flip")
					.equalsIgnoreCase("0")) {
				return !inbound;
			}
		}
		return inbound;
	}

	/**
	 * Returns true if there is more then one reader represented in the values
	 * of the collection "tags".
	 * 
	 * @param tags
	 * @return
	 */
	protected boolean isGhost(List<CSCTag> tags) {
		if (this.uniqueReaders(tags).size() > 1) {
			return true;
		}
		return false;
	}

	/**
	 * Handles the tags if we get a ghost read.
	 */
	protected void handleGhost(List<CSCTag> tags) {
		RifidiLogEntryCreationUtility utility = new RifidiLogEntryCreationUtility();
		logFile.writeToGhostLog(utility.createGhostReadLogEntry(tags.get(0)
				.getEpc(), this.uniqueReaders(tags)));
	}

	/**
	 * Figures out if the given tag is on a watch list.
	 */
	protected boolean onWatchlist(List<CSCTag> tags) {
		// load watchlist file into Set
		// iterate of set to see if id is in there

		// TODO: Currently this will take the contents of the watch list and see
		// if they match up to the tag chars 2-11. In the future we want to
		// make it so they support regexs, which will make things very simple
		// for us and very customizable for them.
		String id = tags.get(0).getEpc();
		List<String> watchList = this.watchlist_reader.getWatchlistTags();
		for (String watchTag : watchList) {
			if (id.length() >= 12) {
				if (id.substring(1, 11).equalsIgnoreCase(watchTag)) {
					logger.debug("On watch list");
					return true;
				}
			} else {
				logger.debug("not on watchlist");
				return false;
			}
		}
		return false;
	}

	/*
	 * Returns all unique readers that are contained in a given list of CSCTags.
	 */
	private Set<String> uniqueReaders(List<CSCTag> tags) {
		Set<String> readerIDs = new HashSet<String>();
		for (CSCTag tag : tags) {
			readerIDs.add(tag.getReaderID());
		}
		return readerIDs;
	}

	/**
	 * Write to the log file. Which log file to write to (the direction file or
	 * the general file, or both) should be determined in this method.
	 * 
	 * @param epc
	 * @param readerID
	 * @param direction
	 * @param onWatchList
	 */
	protected void writeLog(String epc, String readerID, boolean inbound,
			boolean onWatchList) {
		RifidiLogEntryCreationUtility utility = new RifidiLogEntryCreationUtility();
		if (!onWatchList) {
			// Only writes the SAP entry if the tag is not on the watch list
			String sapEntry = utility.createSAPEntry(epc, inbound);
			logFile.writeToSAPLog(sapEntry);
		}

		String standardEntry = utility.createStandardLogEntry(epc, readerID,
				inbound, onWatchList);
		logFile.writeToStandardLog(standardEntry);
	}

	/**
	 * Light up one of the LEDs on the light stack. Which light gets lit is
	 * determined by this method.
	 * 
	 * @param readerID
	 * @param onWatchList
	 * @param direction
	 */
	protected abstract void triggerLight(String readerID, boolean onWatchList,
			boolean inbound);

	/**
	 * Called by spring
	 * 
	 * @param esperService
	 *            the esperService to set
	 */
	public void setLogFile(CSCLogger logFile) {
		this.logFile = logFile;
	}

	/**
	 * @param gpoController
	 *            the gpoController to set
	 */
	public void setGpoController(AbstractGPIOService<?> gpoController) {
		this.gpoController = gpoController;
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
}
