/**
 * 
 */
package org.rifidi.edge.app.db;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.app.db.dao.RFID_DAO;
import org.rifidi.edge.app.db.domain.RFIDEvent;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.notification.data.EPCGeneration1Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

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
public class DBApp {

	/** Esper service */
	private volatile EsperManagementService esperService;
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/** The Data Access Object */
	private volatile RFID_DAO rfidDAO;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(DBApp.class);

	/**
	 * The constructor
	 */
	public DBApp() {
		Activator.myApp = this;
	}

	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime.
	 */
	public void start() {
		if (rfidDAO == null || esperService == null) {
			logger.warn("Cannot start DBApp. "
					+ "Either DAO or EsperService is null");
		}

		// submit a query to get all tags
		EPStatement queryAllTags = esperService.getProvider()
				.getEPAdministrator().createEPL(
						"select * from ReadCycle[select * from tags]");
		// add a listener to the above statement
		queryAllTags.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean b : arg0) {
						Object o = b.getUnderlying();

						// if we have seen a tag, create a new RFIDEvent, and
						// add it to the DAO
						if (o instanceof TagReadEvent) {
							TagReadEvent tre = (TagReadEvent) o;
							RFIDEvent e = new RFIDEvent();
							e.setAntenna(tre.getAntennaID());
							e.setId(((EPCGeneration1Event) tre.getTag())
									.getEpc());
							e.setTimestamp(tre.getTimestamp());
							e.setReader(tre.getReaderID());
							rfidDAO.addRow(e);
						}
					}
				}

			}
		});
		statements.add(queryAllTags);
	}

	/**
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
	 * @param rfidDAO
	 *            the rfidDAO to set
	 */
	public void setRfidDAO(RFID_DAO rfidDAO) {
		this.rfidDAO = rfidDAO;
	}

}
