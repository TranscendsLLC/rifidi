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

public class ReaderPluginServiceImpl implements ReaderPluginService {

	private Log logger = LogFactory.getLog(ReaderPluginServiceImpl.class);
	private HashMap<String, ReaderPlugin> registry = new HashMap<String, ReaderPlugin>();
	private ArrayList<ReaderPluginListener> listeners = new ArrayList<ReaderPluginListener>();

	@Override
	public void registerReaderPlugin(Class<? extends ReaderInfo> readerInfo,
			ReaderPlugin plugin) {
		registry.put(readerInfo.getName(), plugin);
		logger.debug("ReaderPlugin registered "
				+ plugin.getClass().getSimpleName() + " : "
				+ readerInfo.getName());
		for(ReaderPluginListener l : listeners){
			l.readerPluginRegisteredEvent(readerInfo);
		}
	}

	@Override
	public void unregisterReaderPlugin(Class<? extends ReaderInfo> readerInfo) {
		ReaderPlugin plugin = registry.remove(readerInfo.getName());
		logger.debug("ReaderPlugin unregistered "
				+ plugin.getClass().getSimpleName() + " : "
				+ readerInfo.getName());
		for(ReaderPluginListener l : listeners){
			l.readerPluginUnregisteredEvent(readerInfo);
		}

	}

	@Override
	public List<String> getAllReaderInfos() {
		return new ArrayList<String>(registry.keySet());
	}

	@Override
	public ReaderPlugin getReaderPlugin(Class<? extends ReaderInfo> readerInfo) {
		return registry.get(readerInfo.getName());
	}

	@Override
	public void addReaderPluginListener(ReaderPluginListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeReaderPluginListener(ReaderPluginListener listener) {
		listeners.remove(listener);
	}

}
