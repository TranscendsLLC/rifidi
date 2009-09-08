/*
 * 
 * CompositeSensor.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.sensors;

import java.util.Set;

public interface CompositeSensor {

	/**
	 * Get the names of child sensors this sensor has.
	 * 
	 * @return
	 */
	Set<String> getChildren();

}