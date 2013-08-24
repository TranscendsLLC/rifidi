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
