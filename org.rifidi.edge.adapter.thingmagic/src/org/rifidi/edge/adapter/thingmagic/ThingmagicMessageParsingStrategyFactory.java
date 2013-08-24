/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.adapter.thingmagic;

import org.rifidi.edge.sensors.sessions.MessageParsingStrategy;
import org.rifidi.edge.sensors.sessions.MessageParsingStrategyFactory;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class ThingmagicMessageParsingStrategyFactory implements
		MessageParsingStrategyFactory {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.sensors.base.threads.MessageParsingStrategyFactory#createMessageParser()
	 */
	@Override
	public MessageParsingStrategy createMessageParser() {
		return new ThingmagicMessageParsingStrategy();
	}

}
