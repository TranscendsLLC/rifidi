/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
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
