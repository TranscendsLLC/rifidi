/*
 *  ALEPhysicalTagModelAdapterFactory.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields.adapters;

import java.util.HashMap;

/**
 * This is an OSGI service that stores ALEPhysicalTagModelAdapter. Bundles that
 * have classes which implement ALEPhysicalTagModelAdapter should register them
 * with this service when they start up.
 * 
 * Adapters should be registered under PhysicalTagModel.getName()
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ALEPhysicalTagModelAdapterFactory {

	/**
	 * The hashmap of adapters
	 */
	private HashMap<String, ALEPhysicalTagModelAdapter> _adapters = new HashMap<String, ALEPhysicalTagModelAdapter>();

	/**
	 * Register a new adapter with this service
	 * 
	 * @param name
	 *            The name of the PhysicalTagModel this adapter is for. Use
	 *            PhsyicalTagModel.getName()
	 * @param adpater
	 *            The adapter to register
	 */
	public void registerAdapter(String name, ALEPhysicalTagModelAdapter adpater) {
		_adapters.put(name, adpater);
	}

	/**
	 * Remote an adapter from this service
	 * 
	 * @param name
	 *            The name of the PhysicalTagModel this adapter is for
	 */
	public void undregisterAdapter(String name) {
		_adapters.remove(name);
	}

	/**
	 * Retrieve an adapter from this service
	 * 
	 * @param name
	 *            the name of the PhysicalTagModel that the adapter is for
	 * @return The PhysicalTagModelAdapter
	 */
	public ALEPhysicalTagModelAdapter getAdapter(String name) {
		return _adapters.get(name);
	}

}
