package org.rifidi.edge.adapter.thingmagic;

import org.rifidi.edge.core.readerAdapter.commands.ICustomCommand;

public class ThingMagicCustomCommand implements ICustomCommand {
	
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
