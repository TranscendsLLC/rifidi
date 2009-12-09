/*
 * 
 * LoggingServiceImpl.java
 *  
 * Created:     September 14th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.services.logging.impl;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.rifidi.edge.core.services.logging.LoggingService;

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
