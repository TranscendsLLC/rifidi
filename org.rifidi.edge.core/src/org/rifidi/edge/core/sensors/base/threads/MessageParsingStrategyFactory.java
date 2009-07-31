/**
 * 
 */
package org.rifidi.edge.core.sensors.base.threads;

/**
 * This interface allows sensor plugins to produce new MessageParsingStrategies.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface MessageParsingStrategyFactory {

	/**
	 * Create a new instance of a MessageParsingStrategy.
	 * 
	 * @return
	 */
	public MessageParsingStrategy createMessageParser();
}
