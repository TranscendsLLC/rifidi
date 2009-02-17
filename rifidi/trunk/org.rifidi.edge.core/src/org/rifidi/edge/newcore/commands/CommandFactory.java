/**
 * 
 */
package org.rifidi.edge.newcore.commands;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public interface CommandFactory<T extends Command> {
	/**
	 * Get a new instance of the command.
	 * @return
	 */
	T getCommand();
}
