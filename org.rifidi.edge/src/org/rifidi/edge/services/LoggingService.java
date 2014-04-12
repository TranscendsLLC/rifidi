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
