package org.rifidi.edge.readerPlugin.alien.command;

import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;

public class AlienCustomCommand implements ICustomCommand {

	/**
	 * The UID
	 */
	private static final long serialVersionUID = -6640601794117802867L;
	
	/**
	 * The command. 
	 */
	private String command;

	/**
	 * Gets the command. 
	 * 
	 * @return the command
	 */
	public String getCommand() {
		return command;
	}

	/**
	 * Sets the command. 
	 * 
	 * @param command the command to set
	 */
	public void setCommand(String command) {
		this.command = command;
	}
	
}
