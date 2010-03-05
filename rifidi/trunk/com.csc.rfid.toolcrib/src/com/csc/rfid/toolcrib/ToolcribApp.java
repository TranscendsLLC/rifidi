/**
 * 
 */
package com.csc.rfid.toolcrib;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.esper.EsperManagementService;

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
	/** This class writes anything we find to a file */
	private final RifidiLogger logwriter = new RifidiLogger(System
			.getProperty("org.rifidi.logfile"));
	private static final Log logger = LogFactory.getLog(ToolcribApp.class);
	private final DirectionAlgorithm algorithm = new DirectionAlgorithm();
	private AlienGPOController gpoController;

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
		// TODO: Add velocity information to be stored in this window
		statements
				.add(esperService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"create window tags.win:keepall()"
										+ "(tag_ID String, speed Float, rssi String, readerID String)"));

		// esper statement taht adds information to the window
		// TODO make sure that velocity info is inserted into the window
		statements
				.add(esperService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"on ReadCycle[select * from tags]"
										+ "insert into tags select cast(tag.epc?, String) as tag_ID , "
										+ "cast(extraInformation('Speed'),Float) as speed , cast(extraInformation('RSSI'),String) as rssi, readerID as readerID"));

		// esper statement that removes all tags with a given ID from the window
		// if the tag has not been seen at the antenna in the last 60 seconds
		statements.add(esperService.getProvider().getEPAdministrator()
				.createEPL(
						"on pattern [every tag=tags ->" + "(timer:interval("
								+ System.getProperty("org.rifidi.time")
								+ " sec) and not tags"
								+ "(tag_ID = tag.tag_ID))]"
								+ "delete from tags where "
								+ "tag_ID = tag.tag_ID"));

		// esper statement that listens to add and remove events from the window
		EPStatement queryAllTags = esperService.getProvider()
				.getEPAdministrator().createEPL("select irstream * from tags");

		// add a listener to the above statement
		queryAllTags.addListener(new StatementAwareUpdateListener() {

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

				int direction = calculateDirection(tags);
				boolean onWatchList = false;

				if (onWatchlist(tags)) {
					onWatchList = true;
				}

				triggerLight(tags.get(0).getEpc(), onWatchList, direction);
				writeLog(tags.get(0).getEpc(), tags.get(0).getReaderID(),
						direction, onWatchList);

			}
		});
		statements.add(queryAllTags);
	}

	private boolean isGhost(Collection<CSCTag> tags) {

		return false;
	}

	private void handleGhost(Collection<CSCTag> tags) {

	}

	private boolean onWatchlist(List<CSCTag> tags) {
		String id = tags.get(0).getEpc();
		// load wathclist file into Set
		// iterate of set to see if id is in there
		return false;
	}

	private void writeLog(String epc, String readerID, int direction,
			boolean onWatchList) {

	}

	private void triggerLight(String readerID, boolean onWatchList,
			int direction) {
		// map to interactive
		// flashGPO(interactiveReaderID, pin, seconds)
	}

	/**
	 * >0 is incoming 0 is not sure <0 is outgoing
	 * 
	 * @param tags
	 * @return
	 */
	private Integer calculateDirection(Collection<CSCTag> tags) {
		/*
		 * for (String key : speedMap.keySet()) { // if ghostread // handle
		 * ghostread // exit // if tag is on badList // handle badlist event
		 * 
		 * Float speed = algorithm.getSpeed(speedMap.get(key)); if (speed > 0) {
		 * logwriter.writeToFile(key, true); logger.debug("Tag is: " + key +
		 * ", Speed is: " + speed + ", Direction is: Incoming"); } else {
		 * logwriter.writeToFile(key, false); logger.debug("Tag is: " + key +
		 * ", Speed is: " + speed + ", Direction is: Outgoing"); } }
		 */return 0;
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
	 * @param gpoController the gpoController to set
	 */
	public void setGpoController(AlienGPOController gpoController) {
		this.gpoController = gpoController;
	}
}
