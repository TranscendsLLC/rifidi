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
public abstract class AbstractCommandConfiguration<T extends Command> extends
		RifidiService {
	/**
	 * Get a new instance of the command.
	 * 
	 * @return
	 */
	public abstract T getCommand();

	/**
	 * Get the name of the command
	 * 
	 * @return
	 */
	public abstract String getCommandName();

	/**
	 * Get the description of the command.
	 * 
	 * @return
	 */
	public abstract String getCommandDescription();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.RifidiService#destroy()
	 */
	@Override
	public void destroy() {
		// TODO: we also need to figure out how to tell commands that have been
		// produced to not run the next time they are executed
		super.unregister();
	}

}
