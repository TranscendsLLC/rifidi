/**
 * 
 */
package org.rifidi.edge.core.readersession.impl;

import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;

/**
 * 
 * This is a wrapper class for a command that contains all of the information
 * about it for use in the Reader Session
 * 
 * @author kyle
 * 
 */
public class CommandWrapper {

	private String commandName;

	private long commandID;

	private Command command;
	
	private CommandStatus commandStatus;

	private CommandConfiguration configuration;

	public String getCommandName() {
		return commandName;
	}

	public void setCommandName(String commandName) {
		this.commandName = commandName;
	}

	public long getCommandID() {
		return commandID;
	}

	public void setCommandID(long commandID) {
		this.commandID = commandID;
	}

	public Command getCommand() {
		return command;
	}

	public void setCommand(Command command) {
		this.command = command;
	}

	public CommandStatus getCommandStatus() {
		return commandStatus;
	}

	public void setCommandStatus(CommandStatus commandStatus) {
		this.commandStatus = commandStatus;
	}
	
	public void setConfiguration(CommandConfiguration configuration){
		this.configuration = configuration;
	}

	public CommandConfiguration getConfiguration() {
		return this.configuration;
	}

}
