/*
 *  AmbientBarcodeMessageProcessingStrategy.java
 *
 *  Created:	Apr 28, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.ambient.barcode;

import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;

/**
 * Message processing object for the Ambient barcode reader. This class takes
 * byte arrays representing a physical barcode and converts them into a tag
 * object that can be used by Rifidi Edge.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AmbientBarcodeMessageProcessingStrategy implements
		MessageProcessingStrategy {

	private AmbientBarcodeReaderSession session = null;

	/**
	 * 
	 * @param session
	 * @param template
	 */
	public AmbientBarcodeMessageProcessingStrategy(SensorSession session) {
		this.session = (AmbientBarcodeReaderSession) session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy#
	 * processMessage(byte[])
	 */
	@Override
	public void processMessage(byte[] message) {
		this.session.sendTag(message);
	}

}
