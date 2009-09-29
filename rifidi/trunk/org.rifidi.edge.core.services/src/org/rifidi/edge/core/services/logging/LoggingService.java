/*
 * 
 * LoggingService.java
 *  
 * Created:     September 14th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.services.logging;

/**
 * @author kyle
 * 
 */
public interface LoggingService {
	/**
	 * This method sets the logging level for a given logger. The logger name
	 * is, by convention, the class name of the class which is doing the
	 * logging. It is also possible to change many loggers in the same package
	 * as well by supplying a package name instead of a class name
	 * 
	 * @param loggerName
	 *            A class or package name. If null, set the root logger to the
	 *            supplied logging level
	 * @param level
	 *            fatal, error, warn, info, debug, or trace
	 */
	void setLoggingLevel(String loggerName, String level);
}
