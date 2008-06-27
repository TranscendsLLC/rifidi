/*
 *  ReaderPluginFactory.java
 *
 *  Created:	Jun 19, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.readerPlugin.factory;

import org.rifidi.edge.core.exception.readerConnection.RifidiReaderPluginCreationException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.ISpecificReaderPluginFactory;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author jerry
 *
 */
public class ReaderPluginFactory {

	private ReaderPluginRegistryService readerPluginRegistryService;

	public ReaderPluginFactory() {
		ServiceRegistry.getInstance().service(this);
	}

	//TODO method name needs to be changed.
	/**
	 * Creates a reader plugin
	 * @param abstractConnnectionInfo The connection info for this reader
	 * @return A new reader plugin
	 */
	public IReaderPlugin createReaderAdapter(
			AbstractReaderInfo abstractConnnectionInfo) {
		IReaderPlugin readerAdapter = null;

		if (readerPluginRegistryService != null) {
			ISpecificReaderPluginFactory factory = readerPluginRegistryService
					.getSpecReaderAdapterFactory(abstractConnnectionInfo);
			try {
				readerAdapter = factory
						.createSpecificReaderAdapter(abstractConnnectionInfo);
			} catch (RifidiReaderPluginCreationException e) {
				// TODO Catch or toss it up the call stack...
				e.printStackTrace();
			}
		}

		return readerAdapter;
	}

	/**
	 * @return the readerAdapterRegistryService
	 */
	public ReaderPluginRegistryService getReaderAdapterRegistryService() {
		return readerPluginRegistryService;
	}

	/**
	 * @param readerPluginRegistryService
	 *            the readerAdapterRegistryService to set
	 */
	@Inject
	public void setReaderAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;
	}

}
