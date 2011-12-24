/*
 * 
 * JMXService.java
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
 * Interface for a JMX Service to implement
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface JMXService {
	void publish(Configuration config);

	void unpublish(Configuration config);

	/**
	 * Set the reference to the configuration control mbean.
	 * 
	 * @param mbean
	 */
	public void setConfigurationControlMBean(ConfigurationControlMBean mbean);

}
