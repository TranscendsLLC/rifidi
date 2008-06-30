/*
 *  ReaderPluginRegistryServiceImpl.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.service.readerplugin.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;
import org.rifidi.edge.core.readerPlugin.registry.ReaderPluginRegistryChangeListener;
import org.rifidi.edge.core.service.readerplugin.ReaderPluginRegistryService;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public class ReaderPluginRegistryServiceImpl implements
		ReaderPluginRegistryService {

	private Log logger = LogFactory
			.getLog(ReaderPluginRegistryServiceImpl.class);

	private HashMap<Class<? extends AbstractReaderInfo>, ISpecificReaderPluginFactory> registry = new HashMap<Class<? extends AbstractReaderInfo>, ISpecificReaderPluginFactory>();
	
	private Set<ReaderPluginRegistryChangeListener> listeners = new HashSet<ReaderPluginRegistryChangeListener>();

	/*
	 * (non-Javadoc)ReaderConnectionRegistryService
	 * 
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#registerReaderAdapter(java.lang.Class,
	 *      org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory)
	 */
	@Override
	public void registerReaderAdapter(
			Class<? extends AbstractReaderInfo> specificConnectionInfo,
			ISpecificReaderPluginFactory specificReaderAdapterFactory) {
		if (!registry.containsKey(specificConnectionInfo)) {
			logger.debug("ReaderAdapter " + specificConnectionInfo.getName()
					+ " registered");
			registry.put(specificConnectionInfo, specificReaderAdapterFactory);
		
			for (ReaderPluginRegistryChangeListener listener: listeners){
				listener.readerPluginRegistryAddEvent(specificConnectionInfo);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#unregisterReaderAdapter(java.lang.Class)
	 */
	@Override
	public void unregisterReaderAdapter(
		Class<? extends AbstractReaderInfo> specificConnectionInfo) {
		// TODO lookup if there are any instances of the readerPlugin which got
		// just removed
		if (registry.containsKey(specificConnectionInfo) ){
			logger.debug("ReaderAdapter " + specificConnectionInfo.getName()
					+ " unregistered");
			for (ReaderPluginRegistryChangeListener listener: listeners){
				listener.readerPluginRegistryRemoveEvent(specificConnectionInfo);
			}
			registry.remove(specificConnectionInfo);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService#getSpecReaderAdapterFactory(org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo)
	 */
	@Override
	public ISpecificReaderPluginFactory getSpecReaderAdapterFactory(
			AbstractReaderInfo specificConnectionInfo) {
		return registry.get(specificConnectionInfo.getClass());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService#getAvailableReaderAdapters()
	 */
	@Override
	public List<String> getAvailableReaderAdapters() {
		ArrayList<String> availableAdapters = new ArrayList<String>();
		for (Class<?> clazz : registry.keySet()) {
			availableAdapters.add(clazz.getName());
		}
		return availableAdapters;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService#getAbstractReaderInfoClasses()
	 */
	@Override
	public Class<? extends AbstractReaderInfo>[] getAbstractReaderInfoClasses() {
		@SuppressWarnings("unchecked")
		Class<? extends AbstractReaderInfo>[] retVal = new Class[registry
				.size()];
		int index = 0;
		for (Class<? extends AbstractReaderInfo> c : registry.keySet()) {
			retVal[index] = c;
			index++;
		}
		return retVal;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService#addEventListener(org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryChangeListener)
	 */
	@Override
	public void addEventListener(ReaderPluginRegistryChangeListener listener) {
		listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService#removeEventListener(org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryChangeListener)
	 */
	@Override
	public void removeEventListener(ReaderPluginRegistryChangeListener listener) {
		listeners.remove(listener);
	}

}
