/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.services;

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
