/*
 * 
 * MessageProcessingStrategyFactory.java
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
