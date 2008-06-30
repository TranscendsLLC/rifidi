package org.rifidi.edge.core.readerPluginService;

import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public interface ReaderPluginRegistryChangeListener {
	public void readerPluginRegistryAddEvent(
			Class<? extends AbstractReaderInfo> info);

	public void readerPluginRegistryRemoveEvent(
			Class<? extends AbstractReaderInfo> info);

}
