/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.configuration;

import java.util.Map;

import javax.management.DynamicMBean;


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
