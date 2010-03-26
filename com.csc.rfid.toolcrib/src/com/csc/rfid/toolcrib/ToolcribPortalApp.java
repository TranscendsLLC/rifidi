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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

import com.espertech.esper.client.EPStatement;

/**
 * This is a part of the Toolcrib application designed specifically for the
 * portal. It will handle the ESPER statements needed to get the required
 * information and the logic needed to calculate the directionality of the tag.
 * 
 * @author Matthew Dean
 */
public class ToolcribPortalApp extends ToolcribApp {

	private static final Log logger = LogFactory
			.getLog(ToolcribPortalApp.class);

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.csc.rfid.toolcrib.ToolcribApp#determineDirection(org.rifidi.edge.
	 * core.services.notification.data.gpio.GPIEvent)
	 */
	@Override
	protected boolean determineDirection(GPIEvent gpievent) {
		boolean inbound = false;
		if (gpievent.getPort() == 1) {
			inbound = true;
		}
		return inbound;
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
	}

}
