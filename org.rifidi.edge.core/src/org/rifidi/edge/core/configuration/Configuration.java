/*
 * 
 * Configuration.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.configuration;

import java.util.Map;

import javax.management.DynamicMBean;

import org.rifidi.edge.core.configuration.listeners.AttributesChangedListener;

/**
 * Configurations provide a standard interface for handling services through a
 * configuration/management interface.
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public interface Configuration extends DynamicMBean {

	/**
	 * Get the unique name of the configuration.
	 * 
	 * @return
	 */
	String getServiceID();

	/**
	 * Get the id of the factory that registered the configuration.
	 * 
	 * @return
	 */
	String getFactoryID();

	/**
	 * Used for persistence. Should only be called when saving because it
	 * returns attributes as a Map<String,String>.
	 * 
	 * @return
	 */
	Map<String, Object> getAttributes();

	/**
	 * Destroy the service and remove it from the registry.
	 */
	void destroy();

	/**
	 * Get the names of all the DynamicMBean Properties. For use with the
	 * DynamicMBean getAttributes(String[]) method.
	 * 
	 * @return An array of names of all the properties in this Configuration
	 */
	String[] getAttributeNames();

	/**
	 * Add a listener that is notified when the Attributes are changed on the
	 * configuration. Remove this method when AspectJ is available.
	 * 
	 * @param listener
	 */
	void addAttributesChangedListener(
			AttributesChangedListener listener);

	/**
	 * Remote a listener that is notified of Attribute changes on this
	 * configuration. Remove this method when AspectJ is available.
	 * 
	 * @param listener
	 */
	void removeAttributesChangedListener(
			AttributesChangedListener listener);
}
