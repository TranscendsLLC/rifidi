package org.rifidi.edge.core.readerplugin.commands;

/**
 * This is the return status of a command. It is returned by the stop() method
 * of the Command.  It should not be confused with CommandStatus
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public enum CommandReturnStatus {
	/**
	 * Command was successful executed
	 */
	SUCCESSFUL,
	/**
	 * Command was not successful executed
	 */
	UNSUCCESSFUL,
	/**
	 * Command was interrupted while executing
	 */
	INTERRUPTED
}
