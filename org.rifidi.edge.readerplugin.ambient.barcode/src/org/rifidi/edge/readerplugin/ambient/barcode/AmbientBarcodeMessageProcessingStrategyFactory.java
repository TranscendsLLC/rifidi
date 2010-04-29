/*
 *  AmbientBarcodeMessageProcessingStrategyFactory.java
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

import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategyFactory;

/**
 * Factory to generate MessageProcessingStrategy classes.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AmbientBarcodeMessageProcessingStrategyFactory implements
		MessageProcessingStrategyFactory {

	/** The session */
	private AmbientBarcodeReaderSession session;

	/**
	 * 
	 */
	public AmbientBarcodeMessageProcessingStrategyFactory(
			AmbientBarcodeReaderSession session) {
		this.session = session;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.sessions.MessageProcessingStrategyFactory
	 * #createMessageProcessor()
	 */
	@Override
	public MessageProcessingStrategy createMessageProcessor() {
		return new AmbientBarcodeMessageProcessingStrategy(session);
	}

}
