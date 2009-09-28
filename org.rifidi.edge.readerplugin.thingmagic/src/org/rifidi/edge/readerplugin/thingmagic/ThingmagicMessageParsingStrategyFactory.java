/*
 *  ThingmagicMessageParsingStrategyFactory.java
 *
 *  Created:	Sep 27, 2009
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.thingmagic;

import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicMessageParsingStrategyFactory implements
		MessageParsingStrategyFactory {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory#createMessageParser()
	 */
	@Override
	public MessageParsingStrategy createMessageParser() {
		return new ThingmagicMessageParsingStrategy();
	}

}
