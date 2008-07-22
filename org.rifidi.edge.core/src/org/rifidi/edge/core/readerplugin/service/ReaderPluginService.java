package org.rifidi.edge.core.readerplugin.service;

import java.util.List;

import org.osgi.framework.Bundle;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.xml.ReaderPlugin;

/**
 * Service used to keep track of the different ReaderPlugins available to the
 * runtime. If a new ReaderPlugin comes online it should register itself to this
 * service. The service is used to make new instances of the different
 * ReaderPlugins and to give a overview over the known ReaderPlugins. It also
 * supports Listeners with information if a ReaderPlugin comes online and when
 * it disappears again. ReaderPluginService Interface provides the minimal set
 * of functions this service should provide.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface ReaderPluginService {

	/**
	 * Register a new ReaderPlugin to the ReaderPluginService
	 * 
	 * @param readerInfo
	 *            ReaderInfo Class of this ReaderPlugin
	 * @param plugin
	 *            instance of the Plugin Class for the ReaderPlugin
	 */
	public void registerReaderPlugin(Bundle readerPluginBundle);

	/**
	 * Unregister a previously registered ReaderPlugin
	 * 
	 * @param readerInfo
	 *            the ReaderInfoClass of the ReaderPlugin to unregister
	 */
	public void unregisterReaderPlugin(Bundle readerPluginBundle);

	/**
	 * Get the ReaderPlugin to the appropriate ReaderInfo
	 * 
	 * @param readerInfo
	 *            ReaderInfo of the Plugin to lookup
	 * @return the ReaderPlugin associated with this ReaderPlugin
	 */
	public ReaderPlugin getReaderPlugin(String readerInfo);

	/**
	 * Get a list of all available ReaderInfo Classes
	 * 
	 * @return the ReaderInfo class names
	 */
	public List<String> getAllReaderInfos();

	/**
	 * Add a new Listener for new ReaderPlugins and ReaderPlugin remove Events
	 * 
	 * @param listener
	 *            add listener for ReaderPluginService events
	 */
	public void addReaderPluginListener(ReaderPluginListener listener);

	/**
	 * Remove a previously added ReaderPluginListener
	 * 
	 * @param listener
	 *            to remove
	 */
	public void removeReaderPluginListener(ReaderPluginListener listener);
}
