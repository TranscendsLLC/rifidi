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
