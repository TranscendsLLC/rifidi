/**
 * 
 */
package org.rifidi.edge.newcore.commands;


/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface CommandConfiguration<T extends Command> {
	/**
	 * Return a new instance of the configured command on each call.
	 * 
	 * @return
	 */
	T getCommand();
}
