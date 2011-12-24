/*
 * 
 * ConfigurationControlMBean.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */

package org.rifidi.edge.configuration;

/**
 * Configuration management.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface ConfigurationControlMBean {
	/**
	 * Save the current config.
	 */
	public void save();

	/**
	 * Reload configuration from file.
	 */
	public void reload();
}
