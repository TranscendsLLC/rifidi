package org.rifidi.edge.core.readerplugin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginListener;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;

/**
 * This is the implementation of the ReaderPluginService. It keeps track of
 * currently available ReaderPlugins and provides information helping to create
 * instances of the different types of ReaderPlugins. It also allows to monitor
 * the creation and the removal of ReaderPlugins.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ReaderPluginServiceImpl implements ReaderPluginService {

	private Log logger = LogFactory.getLog(ReaderPluginServiceImpl.class);
	private HashMap<String, ReaderPlugin> registry = new HashMap<String, ReaderPlugin>();
	private ArrayList<ReaderPluginListener> listeners = new ArrayList<ReaderPluginListener>();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginService#registerReaderPlugin(java.lang.Class,
	 *      org.rifidi.edge.core.readerplugin.ReaderPlugin)
	 */
	@Override
	public void registerReaderPlugin(Class<? extends ReaderInfo> readerInfo,
			ReaderPlugin plugin) {
		registry.put(readerInfo.getName(), plugin);
		logger.debug("ReaderPlugin registered "
				+ plugin.getClass().getSimpleName() + " : "
				+ readerInfo.getName());
		for (ReaderPluginListener l : listeners) {
			l.readerPluginRegisteredEvent(readerInfo);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginService#unregisterReaderPlugin(java.lang.Class)
	 */
	@Override
	public void unregisterReaderPlugin(Class<? extends ReaderInfo> readerInfo) {
		ReaderPlugin plugin = registry.remove(readerInfo.getName());
		logger.debug("ReaderPlugin unregistered "
				+ plugin.getClass().getSimpleName() + " : "
				+ readerInfo.getName());
		for (ReaderPluginListener l : listeners) {
			l.readerPluginUnregisteredEvent(readerInfo);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginService#getAllReaderInfos()
	 */
	@Override
	public List<String> getAllReaderInfos() {
		return new ArrayList<String>(registry.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginService#getReaderPlugin(java.lang.Class)
	 */
	@Override
	public ReaderPlugin getReaderPlugin(Class<? extends ReaderInfo> readerInfo) {
		return registry.get(readerInfo.getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginService#addReaderPluginListener(org.rifidi.edge.core.readerplugin.service.ReaderPluginListener)
	 */
	@Override
	public void addReaderPluginListener(ReaderPluginListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.service.ReaderPluginService#removeReaderPluginListener(org.rifidi.edge.core.readerplugin.service.ReaderPluginListener)
	 */
	@Override
	public void removeReaderPluginListener(ReaderPluginListener listener) {
		listeners.remove(listener);
	}

}
