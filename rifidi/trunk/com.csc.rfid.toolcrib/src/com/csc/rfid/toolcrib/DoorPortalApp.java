/**
 * 
 */
package com.csc.rfid.toolcrib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

import com.espertech.esper.client.EPStatement;

/**
 * @author Owner
 *
 */
public class DoorPortalApp extends ToolcribApp{

	private Log logger = LogFactory.getLog(DoorPortalApp.class);
	
	@Override
	public void start() {
		logger.debug("Starting CSC Door App");
		String doorReaderID = System.getProperty("com.csc.door_reader");
		EPStatement statement = esperService.getProvider().getEPAdministrator().createEPL(
				"");
		
	}
	@Override

	protected boolean determineDirection(GPIEvent gpievent) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	protected void triggerLight(String readerID, boolean onWatchList,
			boolean inbound) {
		// TODO Auto-generated method stub
		
	}
	
	
}
