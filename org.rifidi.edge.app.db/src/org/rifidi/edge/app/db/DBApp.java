/**
 * 
 */
package org.rifidi.edge.app.db;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.app.db.dao.RFIDDAO;
import org.rifidi.edge.app.db.domain.RFIDEvent;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.notification.data.EPCGeneration1Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * @author kyle
 * 
 */
public class DBApp {

	/** Esper service */
	private volatile EsperManagementService esperService;
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	private volatile RFIDDAO rfidDAO;
	private static Log logger = LogFactory.getLog(DBApp.class);

	public DBApp() {
		Activator.myApp = this;
	}

	public void start() {
		if (rfidDAO == null || esperService == null) {
			logger.warn("Cannot start DBApp. "
					+ "Either DAO or EsperService is null");
		}
		EPStatement queryAllTags = esperService.getProvider()
				.getEPAdministrator().createEPL(
						"select * from ReadCycle[select * from tags]");
		queryAllTags.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean b : arg0) {
						Object o = b.getUnderlying();
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
	public void setRfidDAO(RFIDDAO rfidDAO) {
		this.rfidDAO = rfidDAO;
	}

}
