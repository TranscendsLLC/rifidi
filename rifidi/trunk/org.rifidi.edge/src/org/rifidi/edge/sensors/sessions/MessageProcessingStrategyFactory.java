/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
/**
 * 
 */
package org.rifidi.edge.sensors.sessions;

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
