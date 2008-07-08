package org.rifidi.edge.core.readerplugin.service;

import org.rifidi.edge.core.readerplugin.ReaderInfo;

public interface ReaderPluginListener {
	public void readerPluginRegisteredEvent(
			Class<? extends ReaderInfo> readerInfo);

	public void readerPluginUnregisteredEvent(
			Class<? extends ReaderInfo> readerInfo);
}
