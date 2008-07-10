package org.rifidi.edge.core.readersession.impl;

import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;

public interface CommandExecutionListener {

	public void commandFinished(Command command, CommandReturnStatus status);

}
