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

	private String info;

	public String getConnectionManager() {
		return connectionManager;
	}

	public String getInfo() {
		return info;
	}

	public void setConnectionManager(String connectionManager) {
		this.connectionManager = connectionManager;
	}

	public void setInfo(String info) {
		this.info = info;
	}

}
