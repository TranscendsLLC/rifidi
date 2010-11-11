/*
 * AwidMessageParsingStrategyFactory.java
 * 
 * Created:     Oct 20th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.adapter.awid.awid2010;

import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.core.sensors.sessions.MessageParsingStrategyFactory;

/**
 * A factory that produces AwidMessageParsingStrategy objects.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AwidMessageParsingStrategyFactory implements
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
		return new AwidMessageParsingStrategy();
	}

}
