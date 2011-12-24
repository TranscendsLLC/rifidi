/*
 * 
 * MBeanInfoStrategy.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.configuration;

import javax.management.openmbean.OpenMBeanInfoSupport;

/**
 * Strategy pattern used to create an MBeanInfo for a given class.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface MBeanInfoStrategy {
	/**
	 * Process the given class to create an MBeanInfo.
	 * 
	 * @param clazz
	 * @return
	 */
	OpenMBeanInfoSupport getMBeanInfo(Class<?> clazz);
}
