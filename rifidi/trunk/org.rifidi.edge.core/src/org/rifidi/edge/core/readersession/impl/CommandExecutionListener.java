package org.rifidi.edge.core.readersession.impl;

import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;

/**
 * The CommandExecutionListener is used to get notified when a command finishes
 * it's execution. It will tell the suscriber about the command finished and the
 * status the command finished with.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface CommandExecutionListener {

	/**
	 * Command command finished with the status.
	 * 
	 * @param command
	 *            command finished
	 * @param status
	 *            status of the command's execution
	 */
	public void commandFinished(Command command, CommandReturnStatus status);

}
