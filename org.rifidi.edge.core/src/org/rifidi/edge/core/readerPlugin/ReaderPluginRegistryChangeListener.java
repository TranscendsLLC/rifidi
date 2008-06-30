package org.rifidi.edge.core.readerPlugin;


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
