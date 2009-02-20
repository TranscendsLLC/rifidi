/**
 * 
 */
package org.rifidi.edge.core.commands;

import org.rifidi.configuration.RifidiService;

/**
 * Command configurations represent all properties of a command and will create
 * instances of the commands with those properties.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface CommandConfiguration<T extends Command> extends RifidiService {
	/**
	 * Get a new instance of the command.
	 * 
	 * @return
	 */
	T getCommand();

	/**
	 * Get the name of the command
	 * 
	 * @return
	 */
	String getCommandName();

	/**
	 * Get the description of the command.
	 * 
	 * @return
	 */
	String getCommandDescription();
}
