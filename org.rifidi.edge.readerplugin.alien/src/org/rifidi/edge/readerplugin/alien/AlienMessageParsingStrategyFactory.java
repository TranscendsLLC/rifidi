/*
 * 
 * AlienMessageParsingStrategyFactory.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory;

/**
 * Produces new AlienMessageParsingStrategies
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienMessageParsingStrategyFactory implements
		MessageParsingStrategyFactory {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.base.threads.MessageParsingStrategyFactory
	 * #createMessageParser()
	 */
	@Override
	public MessageParsingStrategy createMessageParser() {
		return new AlienMessageParsingStrategy();
	}

}
