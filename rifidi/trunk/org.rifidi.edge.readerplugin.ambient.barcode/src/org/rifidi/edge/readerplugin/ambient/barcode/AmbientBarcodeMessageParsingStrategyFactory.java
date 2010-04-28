/*
 *  AmbientBarcodeMessageParsingStrategy.java
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

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;

/**
 * Factory class for creating AmbientBarcodeMessageParsingStrategies.  
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class AmbientBarcodeMessageParsingStrategyFactory implements
		MessageParsingStrategyFactory {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory#createMessageParser()
	 */
	@Override
	public MessageParsingStrategy createMessageParser() {
		return new AmbientBarcodeMessageParsingStrategy();
	}

}
