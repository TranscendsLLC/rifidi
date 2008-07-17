package org.rifidi.edge.core.readerplugin.commands;

/**
 * Status of a command
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
