package org.rifidi.edge.core.readerplugin.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;

public class ReaderPluginServiceImpl implements ReaderPluginService {

	private HashMap<Class<? extends ReaderInfo>, ReaderPlugin> registry = new HashMap<Class<? extends ReaderInfo>, ReaderPlugin>();

	@Override
	public void registerReaderPlugin(Class<? extends ReaderInfo> readerInfo,
			ReaderPlugin plugin) {
		registry.put(readerInfo, plugin);
	}

	@Override
	public void unregisterReaderPlugin(Class<? extends ReaderInfo> readerInfo) {
		registry.remove(readerInfo);

	}

	@Override
	public List<Class<? extends ReaderInfo>> getAllReaderPlugins() {
		return new ArrayList<Class<? extends ReaderInfo>>(registry.keySet());
	}

	@Override
	public ReaderPlugin getReaderPlugin(Class<? extends ReaderInfo> readerInfo){
		//TODO Throw Exception if no such reader
		return registry.get(readerInfo);
	}

	
	
}
