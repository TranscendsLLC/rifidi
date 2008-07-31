package org.rifidi.edge.core.readerplugin.xml;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Base class for extending a ReaderPlugin
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
@XmlRootElement
public class ReaderPluginXML extends ReaderPluginCommandExtension {

	private String connectionManager;

	private String plugin;

	public String getConnectionManager() {
		return connectionManager;
	}

	public String getPlugin() {
		return plugin;
	}

	public void setPlugin(String plugin) {
		this.plugin = plugin;
	}

	public void setConnectionManager(String connectionManager) {
		this.connectionManager = connectionManager.trim();
	}

}
