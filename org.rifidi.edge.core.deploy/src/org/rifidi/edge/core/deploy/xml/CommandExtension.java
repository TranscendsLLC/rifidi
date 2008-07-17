package org.rifidi.edge.core.deploy.xml;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * CommandExtension for extending ReaderPlugins with custom commands
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
@XmlRootElement
public class CommandExtension {

	/**
	 * The ClassName of the ReaderPlugin this extension is aimed for
	 */
	private String extendPlugin;

	/**
	 * List of ClassNames representing the additional custom commands
	 */
	private List<String> commands;

	/**
	 * Get the class name of the ReaderPlugin to extend
	 * 
	 * @return class name of the ReaderPlugin
	 */
	public String getExtendPlugin() {
		return extendPlugin;
	}

	/**
	 * Set the class name of the ReaderPlugin to extend
	 * 
	 * @param extendPlugin
	 *            class name of the ReaderPlugin
	 */
	public void setExtendPlugin(String extendPlugin) {
		this.extendPlugin = extendPlugin;
	}

	/**
	 * Get the Class names for the commands contributing to the ReaderPlugin
	 * 
	 * @return list of class names for the commands
	 */
	public List<String> getCommands() {
		return commands;
	}

	/**
	 * Set the class names for the commands contributing to the PeaderPlugin
	 * 
	 * @param commands
	 *            list of class names for the commands
	 */
	public void setCommands(List<String> commands) {
		this.commands = commands;
	}

}
