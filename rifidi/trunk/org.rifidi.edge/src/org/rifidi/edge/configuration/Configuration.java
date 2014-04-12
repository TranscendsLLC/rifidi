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
