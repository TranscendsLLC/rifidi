/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api;

/**
 * This interface specifies property names that are common across all Rifidi
 * Apps. These properties can be specified in the properties file for each
 * application
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RifidiAppCommonProperties {
	/**
	 * The property for whether or not the application shoul automatically start
	 * when it is loaded.
	 */
	public static final String LAZY_START = "LazyStart";
}
