/**
 * 
 */
package com.csc.rfid.toolcrib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

import com.espertech.esper.client.EPStatement;

/**
 * @author Owner
 *
 */
public class ToolcribWindowApp extends ToolcribApp {

	private static final Log logger= LogFactory.getLog(ToolcribWindowApp.class);
	private static final int error_light=1;
	private static final int outbound_light=2;
	private static final int inbound_light=3;
	
	/* (non-Javadoc)
	 * @see com.csc.rfid.toolcrib.ToolcribApp#start()
	 */
	@Override
	public void start() {
		logger.debug("Starting CSC Window App");
		String windowReaderID = System.getProperty("com.csc.window_reader");
		//String cutOffTime = System.getProperty("com.csc.tagreadcutofftime");
		String cutOffTime="2";
		EPStatement statement = esperService.getProvider().getEPAdministrator().createEPL(
				"select * from pattern [every gpievent=GPIEvent(state=false, readerID='"
				+ windowReaderID + "')->"
				+ "tags=ReadCycle(readerID='" + windowReaderID
				+ "')[select * from tags] "
				+ "until timer:interval(" + cutOffTime
				+ " sec)]");
		statement.addListener(getTagsUpdateListener());

	}

	@Override
	protected boolean determineDirection(GPIEvent gpievent) {
		boolean inbound = false;
		if (gpievent.getPort() == 1) {
			inbound = true;
		}
		return inbound;
	}

	@Override
	protected void triggerLight(String readerID, boolean onWatchList,
			boolean inbound) {
		try {
			if (onWatchList) {
				// Trigger watchlist (red) GPO 2
				this.gpoController
						.flashGPO(readerID, FLASH_TIME, error_light);
			} else if (inbound) {
				// Trigger inbound (yellow) GPO 3
				this.gpoController.flashGPO(readerID, FLASH_TIME,
						inbound_light);
			} else {
				// Trigger outbound (green) GPO 4
				this.gpoController.flashGPO(readerID, FLASH_TIME,
						outbound_light);
			}
		} catch (CannotExecuteException e) {
			logger.warn("Problem executing GPO: " + e.getMessage());
		}
	}
}
