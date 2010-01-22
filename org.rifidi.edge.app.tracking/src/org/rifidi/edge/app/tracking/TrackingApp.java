/**
 * 
 */
package org.rifidi.edge.app.tracking;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.app.tracking.domain.RFIDEvent;
import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This class controls the lifecycle of the Rifidi Application. It handles the
 * submission of esper statements and handles esper events.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TrackingApp {

	/** Esper service */
	private volatile EsperManagementService esperService;
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/** The Data Access Object */
	private volatile JMSSender jmsSender;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(TrackingApp.class);


	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime.
	 * 
	 * This method is called by spring
	 */
	public void start() {
		if (jmsSender == null || esperService == null) {
			logger.warn("Cannot start DBApp. "
					+ "Either DAO or EsperService is null");
		}
		
		//esper statement that creates a window. There are three pieces of 
		//information in each column: tagID, readerID, and antennaID
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window tags.std:firstunique(tag_ID, reader_ID, antenna) " +
				"(reader_ID String, antenna int, tag_ID String)"));
		
		//esper statement taht adds information to the window
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
				"on ReadCycle[select * from tags]" +
				"insert into tags select readerID as reader_ID, " +
				"antennaID as antenna, cast(tag.epc?, String) as tag_ID"));
		
		//esper statement that removes rows from the window if the tag has not been seen at 
		//the antenna in the last 2 seconds
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
				"on pattern [every tag=tags ->" +
					"(timer:interval(2 sec) and not tags" +
						"(tag_ID = tag.tag_ID, reader_ID=tag.reader_ID, antenna=tag.antenna))]" +
				"delete from tags where " +
						"tag_ID = tag.tag_ID AND reader_ID=tag.reader_ID AND antenna=tag.antenna"));
		

		// esper statement that listens to add and remove events from the window
		EPStatement queryAllTags = esperService.getProvider()
				.getEPAdministrator().createEPL(
						"select irstream * from tags");
		
		// add a listener to the above statement
		queryAllTags.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				
				//all additions
				if (arg0 != null) {
					for (EventBean b : arg0) {
						String readerID = (String)b.get("reader_ID");
						Integer antennaID = (Integer)b.get("antenna");
						String epc = (String)b.get("tag_ID");
						RFIDEvent e = new RFIDEvent();
						e.setAntenna(antennaID);
						e.setId(epc);
						e.setReader(readerID);
						jmsSender.arrvied(e);
					}
				}
				//all deletions
				if (arg1 != null) {
					for (EventBean b : arg1) {
						String readerID = (String)b.get("reader_ID");
						Integer antennaID = (Integer)b.get("antenna");
						String epc = (String)b.get("tag_ID");
						RFIDEvent e = new RFIDEvent();
						e.setAntenna(antennaID);
						e.setId(epc);
						e.setReader(readerID);
						jmsSender.departed(e);
					}
				}

			}
		});
		statements.add(queryAllTags);
	}

	/**
	 * Called by spring
	 * Iterate through all statements and stop them.
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

	/**
	 * Called by spring
	 * 
	 * @param jmsSender
	 *            the rfidDAO to set
	 */
	public void setJmsSender(JMSSender jmsSender) {
		this.jmsSender = jmsSender;
	}

}
