package org.rifidi.edge.core.readerplugin.service;

import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;

public interface ReaderPluginService {

	public void registerReaderPlugin(Class<? extends ReaderInfo> readerInfo,
			ReaderPlugin plugin);

	public void unregisterReaderPlugin(Class<? extends ReaderInfo> readerInfo);

	public ReaderPlugin getReaderPlugin(Class<? extends ReaderInfo> readerInfo);

	public List<String> getAllReaderPlugins();

	public void addReaderPluginListener(ReaderPluginListener listener);

	public void removeReaderPluginListener(ReaderPluginListener listener);
}
