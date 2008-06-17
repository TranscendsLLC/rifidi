package org.rifidi.edge.readerPlugin.thingmagic.commands;

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;

public class ThingMagicCustomCommand implements ICustomCommand {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6632279831996948561L;
	
	private String customCommand;

	/**
	 * @return the customCommand
	 */
	public String getCustomCommand() {
		return customCommand;
	}

	/**
	 * @param customCommand the customCommand to set
	 */
	public void setCustomCommand(String customCommand) {
		this.customCommand = customCommand;
	}
	
	
	
}
