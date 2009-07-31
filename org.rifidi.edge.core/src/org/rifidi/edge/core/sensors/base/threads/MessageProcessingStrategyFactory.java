/**
 * 
 */
package org.rifidi.edge.core.sensors.base.threads;

/**
 * Allows sensor plugins to create a message parsing strategies
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface MessageProcessingStrategyFactory {

	/***
	 * Create a new instance of a MessageParsingStrategy
	 * 
	 * @return
	 */
	public MessageProcessingStrategy createMessageProcessor();

}
