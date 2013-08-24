/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.services;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class LoggingServiceImpl implements LoggingService {

	@Override
	public void setLoggingLevel(String loggerName, String level) {
		Level newLevel = Level.toLevel(level);

		// Level.toLevel returns DEBUG by default if the supplied arguemnt is
		// not valid. We want this method to throw an exception if it is not
		// valid
		if (newLevel.toInt() == Level.DEBUG_INT) {
			if (!level.equalsIgnoreCase("debug")) {
				throw new IllegalArgumentException("Invalid level: " + level);
			}
		}
		Logger logger;
		if (loggerName == null) {
			logger = Logger.getRootLogger();
		} else {
			logger = Logger.getLogger(loggerName);
		}
		logger.info("setting logger to " + level);
		logger.setLevel(newLevel);

	}
}
