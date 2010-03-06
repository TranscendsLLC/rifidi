/**
 * 
 */
package com.csc.rfid.toolcrib;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.csc.rfid.toolcrib.utilities.AlienGPOController;
import com.csc.rfid.toolcrib.utilities.DirectionAlgorithm;
import com.csc.rfid.toolcrib.utilities.RifidiLogEntryCreationUtility;
import com.csc.rfid.toolcrib.utilities.RifidiLogger;
import com.csc.rfid.toolcrib.utilities.WatchlistReader;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This application will monitor the incoming and outgoing parts through a
 * window for CSC/UTC. The
 * 
 * @author Matthew Dean - matt@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ToolcribApp {
	/** Esper service */
	private volatile EsperManagementService esperService;
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/** This logger records directionality events only */
	private final RifidiLogger sap_log = new RifidiLogger(System
			.getProperty("org.rifidi.logfile"));
	/** This is the general log file where many events are processed */
	private final RifidiLogger standard_log = new RifidiLogger(System
			.getProperty("org.rifidi.standardlog"));
	private final RifidiLogger ghost_log = new RifidiLogger(System
			.getProperty("org.rifidi.ghostlog"));
	private final RifidiLogger downtime_log = new RifidiLogger(System
			.getProperty("org.rifidi.downtimelog"));
	private final WatchlistReader watchlist_reader = new WatchlistReader(System
			.getProperty("org.rifidi.watchlist"));
	private static final Log logger = LogFactory.getLog(ToolcribApp.class);
	private final DirectionAlgorithm algorithm = new DirectionAlgorithm();
	private AlienGPOController gpoController;

	// GPIO values
	private static final Integer FLASH_TIME = Integer.parseInt(System
			.getProperty("org.rifidi.flashtime"));
	private static final Integer RED_LIGHT_GPO = Integer.parseInt(System
			.getProperty("org.rifidi.redlightgpo"));
	private static final Integer YELLOW_LIGHT_GPO = Integer.parseInt(System
			.getProperty("org.rifidi.yellowlightgpo"));
	private static final Integer GREEN_LIGHT_GPO = Integer.parseInt(System
			.getProperty("org.rifidi.greenlightgpo"));

	/**
	 * Constructor.
	 */
	public ToolcribApp() {
		Activator.myApp = this;
	}

	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime.
	 * 
	 * This method is called by spring
	 */
	public void start() {
		// esper statement that creates a window.
		statements.add(esperService.getProvider().getEPAdministrator()
				.createEPL(
						"create window tags.win:keepall()"
								+ "(tag_ID String, speed Float, rssi "
								+ "String, readerID String)"));

		// esper statement taht adds information to the window
		// TODO make sure that velocity info is inserted into the window
		statements
				.add(esperService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"on ReadCycle[select * from tags]"
										+ "insert into tags select cast(tag.epc?, String) as tag_ID , "
										+ "cast(extraInformation('Speed'),Float) as speed , "
										+ "cast(extraInformation('RSSI'),String) as rssi, "
										+ "readerID as readerID"));

		// esper statement that removes all tags with a given ID from the window
		// if the tag has not been seen at the antenna in the last 60 seconds
		statements
				.add(esperService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"on pattern [every tag=tags ->"
										+ "(timer:interval("
										+ System
												.getProperty("org.rifidi.tagreadcutofftime")
										+ " sec) and not tags"
										+ "(tag_ID = tag.tag_ID))]"
										+ "delete from tags where "
										+ "tag_ID = tag.tag_ID"));

		// esper statement that listens to add and remove events from the window
		EPStatement queryAllTags = esperService.getProvider()
				.getEPAdministrator().createEPL("select irstream * from tags");

		// add a listener to the above statement
		queryAllTags.addListener(new StatementAwareUpdateListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.espertech.esper.client.StatementAwareUpdateListener#update
			 * (com.espertech.esper.client.EventBean[],
			 * com.espertech.esper.client.EventBean[],
			 * com.espertech.esper.client.EPStatement,
			 * com.espertech.esper.client.EPServiceProvider)
			 */
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
						tags.add(cscTag);
					}
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
				inbound = check_orientations(tags.get(0).getReaderID(), inbound);

				if (onWatchlist(tags)) {
					onWatchList = true;
				}

				triggerLight(tags.get(0).getEpc(), onWatchList, inbound);
				writeLog(tags.get(0).getEpc(), tags.get(0).getReaderID(),
						inbound, onWatchList);

			}
		});
		statements.add(queryAllTags);
	}

	/*
	 * Checks if the direction needs to be flipped for the given reader ID. If
	 * it does, the return value will be the opposite of the "inbound" value. If
	 * it isn't, the inbound return value will simply be returned.
	 */
	private boolean check_orientations(String readerID, boolean inbound) {
		if (readerID.equalsIgnoreCase(System
				.getProperty("org.rifidi.window_reader"))) {
			if (!System.getProperty("org.rifidi.window_direction_flip")
					.equalsIgnoreCase("0")) {
				return !inbound;
			}
		} else if (readerID.equalsIgnoreCase(System
				.getProperty("org.rifidi.door_reader"))) {
			if (!System.getProperty("org.rifidi.door_direction_flip")
					.equalsIgnoreCase("0")) {
				return !inbound;
			}
		} else if (readerID.equalsIgnoreCase(System
				.getProperty("org.rifidi.portal_reader"))) {
			if (!System.getProperty("org.rifidi.portal_direction_flip")
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
	private boolean isGhost(List<CSCTag> tags) {
		if (this.uniqueReaders(tags).size() > 1) {
			return true;
		}
		return false;
	}

	/*
	 * Handles the tags if we get a ghost read.
	 */
	private void handleGhost(List<CSCTag> tags) {
		RifidiLogEntryCreationUtility utility = new RifidiLogEntryCreationUtility();
		ghost_log.writeToFile(utility.createGhostReadLogEntry(tags.get(0)
				.getEpc(), this.uniqueReaders(tags)));
	}

	/*
	 * Figures out if the given tag is on a watch list.
	 */
	private boolean onWatchlist(List<CSCTag> tags) {
		// load watchlist file into Set
		// iterate of set to see if id is in there

		// TODO: Currently this will take the contents of the watch list and see
		// if they match up to the tag chars 2-11. In the future we want to
		// make it so they support regexs, which will make things very simple
		// for us and very customizable for them.
		String id = tags.get(0).getEpc();
		List<String> watchList = this.watchlist_reader.getWatchlistTags();
		for (String watchTag : watchList) {
			if (id.substring(1).equalsIgnoreCase(watchTag)) {
				return true;
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
	private void writeLog(String epc, String readerID, boolean inbound,
			boolean onWatchList) {
		RifidiLogEntryCreationUtility utility = new RifidiLogEntryCreationUtility();
		if (!onWatchList) {
			// Only writes the SAP entry if the tag is not on the watch list
			String sapEntry = utility.createSAPEntry(epc, inbound);
			sap_log.writeToFile(sapEntry);
		}

		String standardEntry = utility.createStandardLogEntry(epc, readerID,
				inbound, onWatchList);
		standard_log.writeToFile(standardEntry);
	}

	/**
	 * Light up one of the LEDs on the light stack. Which light gets lit is
	 * determined by this method.
	 * 
	 * @param readerID
	 * @param onWatchList
	 * @param direction
	 */
	private void triggerLight(String readerID, boolean onWatchList,
			boolean inbound) {

		if (onWatchList) {
			// Trigger watchlist (red) GPO 2
			this.gpoController.flashGPO(readerID, RED_LIGHT_GPO, FLASH_TIME);
		} else if (inbound) {
			// Trigger inbound (yellow) GPO 3
			this.gpoController.flashGPO(readerID, YELLOW_LIGHT_GPO, FLASH_TIME);
		} else {
			// Trigger outbound (green) GPO 4
			this.gpoController.flashGPO(readerID, GREEN_LIGHT_GPO, FLASH_TIME);
		}
		// map to interactive
	}

	/**
	 * >0 is incoming 0 is not sure <0 is outgoing
	 * 
	 * @param tags
	 * @return
	 */
	private Float calculateDirection(List<CSCTag> tags) {
		// if ghostread // handle ghostread
		// exit // if tag is on badList // handle badlist event
		String epc = tags.get(0).getEpc();
		List<Float> speeds = new ArrayList<Float>();
		for (CSCTag tag : tags) {
			speeds.add(tag.getSpeed());
		}
		Float speed = algorithm.getSpeed(speeds);

		if (speed > 0) {
			// sap_log.writeToFile(epc, true);
			logger.debug("Tag is: " + epc + ", Speed is: " + speed
					+ ", Direction is: Incoming");
		} else {
			// sap_log.writeToFile(epc, false);
			logger.debug("Tag is: " + epc + ", Speed is: " + speed
					+ ", Direction is: Outgoing");
		}

		return speed;
	}

	/**
	 * Called by spring Iterate through all statements and stop them.
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
		start();
	}

	/**
	 * @return the gpoController
	 */
	public AlienGPOController getGpoController() {
		return gpoController;
	}

	/**
	 * @param gpoController
	 *            the gpoController to set
	 */
	public void setGpoController(AlienGPOController gpoController) {
		this.gpoController = gpoController;
	}
}
