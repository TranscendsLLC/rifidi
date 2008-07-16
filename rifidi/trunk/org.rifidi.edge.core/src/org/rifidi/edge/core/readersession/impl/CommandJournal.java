/**
 * 
 */
package org.rifidi.edge.core.readersession.impl;

import java.util.ArrayList;

import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;

/**
 * @author kyle
 * 
 */
public class CommandJournal {

	ArrayList<CommandWrapper> journal = new ArrayList<CommandWrapper>();

	public void addCommand(CommandWrapper command) {
		journal.add(command);
	}

	public CommandStatus getCommandStatus(long commandID) {
		for (CommandWrapper cw : journal) {
			if (cw.getCommandID() == commandID) {
				return cw.getCommandStatus();
			}
		}
		return CommandStatus.NOCOMMAND;
	}

	public void updateCommand(Command command, CommandReturnStatus status) {
		for (CommandWrapper cw : journal) {
			if (cw.getCommand() == command) {
				cw.setCommandStatus(CommandStatus.toCommandStatus(status));
				break;
			}
		}

	}

	public CommandStatus getCommandStatus(Command command) {
		for (CommandWrapper cw : journal) {
			if (cw.getCommand() == command) {
				return cw.getCommandStatus();
			}
		}
		return CommandStatus.NOCOMMAND;
	}

}
