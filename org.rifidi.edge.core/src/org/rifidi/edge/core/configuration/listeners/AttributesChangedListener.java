/*
 * 
 * AttributesChangedListener.java
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
package org.rifidi.edge.core.configuration.listeners;
import javax.management.AttributeList;

/**
 * This is an ugly side effect of the fact that we need to notify the client of
 * when the attributes of a configuration is changed. Get rid of this when we
 * switch to using AspecJ
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface AttributesChangedListener {
	/**
	 * Called when attributes have been modified on a Configuration
	 * 
	 * @param configurationID
	 *            The ID of the configuration whose ID was modified
	 * @param attributes
	 *            The attributes that were changed
	 */
	void attributesChanged(String configurationID, AttributeList attributes);
}
