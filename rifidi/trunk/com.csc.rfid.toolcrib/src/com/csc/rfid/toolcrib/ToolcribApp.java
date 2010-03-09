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
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.management.AbstractGPIOService;
import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.csc.rfid.toolcrib.utilities.RifidiLogEntryCreationUtility;
import com.csc.rfid.toolcrib.utilities.RifidiLogger;
import com.csc.rfid.toolcrib.utilities.WatchlistReader;

/**
 * This application will monitor the incoming and outgoing parts through a
 * window for CSC/UTC. It will be extended by the modules for the window, door,
 * and portal readers.
 * 
 * @author Matthew Dean - matt@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class ToolcribApp {

	/** Esper service */
	protected volatile EsperManagementService esperService;

	/** This logger records directionality events only */
	protected RifidiLogger logFile;
	private final WatchlistReader watchlist_reader = new WatchlistReader(System
			.getProperty("com.csc.watchlist"));
	private static final Log logger = LogFactory.getLog(ToolcribApp.class);
	/** Controller for GPO of Alien */
	private AbstractGPIOService<?> gpoController;

	// GPIO values
	private static final Integer FLASH_TIME = Integer.parseInt(System
			.getProperty("com.csc.flashtime"));
	private static final Integer RED_LIGHT_GPO = Integer.parseInt(System
			.getProperty("com.csc.redlightgpo"));
	private static final Integer YELLOW_LIGHT_GPO = Integer.parseInt(System
			.getProperty("com.csc.yellowlightgpo"));
	private static final Integer GREEN_LIGHT_GPO = Integer.parseInt(System
			.getProperty("com.csc.greenlightgpo"));

	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime.
	 * 
	 * This method is called by spring
	 */
	public abstract void start();

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

	/*
	 * Handles the tags if we get a ghost read.
	 */
	protected void handleGhost(List<CSCTag> tags) {
		RifidiLogEntryCreationUtility utility = new RifidiLogEntryCreationUtility();
		logFile.writeToGhostLog(utility.createGhostReadLogEntry(tags.get(0)
				.getEpc(), this.uniqueReaders(tags)));
	}

	/*
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
			if (id.length() >= 14) {
				if (id.substring(3, 13).equalsIgnoreCase(watchTag)) {
					return true;
				}
			} else {
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
	protected void triggerLight(String readerID, boolean onWatchList,
			boolean inbound) {

		try {
			if (onWatchList) {
				// Trigger watchlist (red) GPO 2
				this.gpoController
						.flashGPO(readerID, FLASH_TIME, RED_LIGHT_GPO);
			} else if (inbound) {
				// Trigger inbound (yellow) GPO 3
				this.gpoController.flashGPO(readerID, FLASH_TIME,
						YELLOW_LIGHT_GPO);
			} else {
				// Trigger outbound (green) GPO 4
				this.gpoController.flashGPO(readerID, FLASH_TIME,
						GREEN_LIGHT_GPO);
			}
		} catch (CannotExecuteException e) {
			logger.warn("Problem executing GPO: " + e.getMessage());
		}
		// map to interactive
	}

	/**
	 * Called by spring Iterate through all statements and stop them.
	 */
	public abstract void stop();

	/**
	 * Called by spring
	 * 
	 * @param esperService
	 *            the esperService to set
	 */
	public void setLogFile(RifidiLogger logFile) {
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
