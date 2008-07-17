package org.rifidi.edge.core.readerplugin.commands;

/**
 * Status of a command
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public enum CommandReturnStatus {
	/**
	 * Command was successful exceuted
	 */
	SUCCESSFUL,
	/**
	 * Command was not succesful executed
	 */
	UNSUCCESSFUL,
	/**
	 * Command was interrupted while executing
	 */
	INTERRUPTED
}
