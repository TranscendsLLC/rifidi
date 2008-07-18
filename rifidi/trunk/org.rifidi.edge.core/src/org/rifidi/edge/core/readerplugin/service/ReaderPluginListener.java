package org.rifidi.edge.core.readerplugin.service;

import org.rifidi.edge.core.readerplugin.ReaderInfo;

/**
 * Listener for ReaderPlugin Events. This events happen when ReaderPlugins get
 * registered in the ReaderPluginService.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface ReaderPluginListener {

	/**
	 * New ReaderPlugin registered event
	 * 
	 * @param readerInfo ReaderInfo Class of this specific ReaderPlugin
	 */
	public void readerPluginRegisteredEvent(
			Class<? extends ReaderInfo> readerInfo);

	/**
	 * ReaderPlugin unregistered event
	 * 
	 * @param readerInfo the ReaderInfo Class of the ReaderPlugin unregistering
	 */
	public void readerPluginUnregisteredEvent(
			Class<? extends ReaderInfo> readerInfo);
}
