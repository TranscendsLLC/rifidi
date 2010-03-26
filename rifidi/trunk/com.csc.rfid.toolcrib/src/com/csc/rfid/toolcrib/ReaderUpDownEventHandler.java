/*
 *  ReaderUpDownEventHandler.java
 *
 *  Created:	Mar 9, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package com.csc.rfid.toolcrib;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.notification.data.management.SessionDownEvent;
import org.rifidi.edge.core.services.notification.data.management.SessionUpEvent;

import com.csc.rfid.toolcrib.utilities.CSCLogger;
import com.csc.rfid.toolcrib.utilities.RifidiLogEntryCreationUtility;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This class handles the functionality of logging when a networked reader
 * becomes unavailable.
 * 
 * @author Matthew Dean
 */
public class ReaderUpDownEventHandler {

	/** Esper service */
	protected volatile EsperManagementService esperService;
	/** This logger records directionality events only */
	protected CSCLogger logFile;
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(ReaderUpDownEventHandler.class);

	/**
	 * Start the application. This method submits the esper statements to the
	 * esper runtime.
	 * 
	 * This method is called by spring
	 */
	public void start() {
		logger.debug("Starting Toolcrib Downtime App");

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
	}

	/**
	 * A private method that creates a listener for SessionUpEvents and
	 * sessionDownEvents
	 * 
	 * @return
	 */
	private StatementAwareUpdateListener getSessionStateUpdateListener() {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				//if there is an arriving event
				if (arg0 != null) {
					for (EventBean b : arg0) {
						//if we have a SessionUpEvent
						if (b.getUnderlying() instanceof SessionUpEvent) {
							logger.debug(b.getUnderlying());
							SessionUpEvent sue = (SessionUpEvent) b
									.getUnderlying();
							//log the event!
							logFile
									.writeDowntimeLog(RifidiLogEntryCreationUtility
											.createUptimeLogEntry(sue
													.getReaderID(), sue
													.getTimestamp()));
							
							//if we have a SessionDownEvent
						} else if (b.getUnderlying() instanceof SessionDownEvent) {
							logger.debug(b.getUnderlying());
							SessionDownEvent sue = (SessionDownEvent) b
									.getUnderlying();
							
							//log the event!
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
	 * @param esperService
	 *            the esperService to set
	 */
	public void setLogFile(CSCLogger logFile) {
		this.logFile = logFile;
	}

	/**
	 * Called by spring.
	 * Iterate through all statements and stop them.
	 */
	public void stop() {
		for (EPStatement statement : statements) {
			statement.destroy();
		}
	}
}
