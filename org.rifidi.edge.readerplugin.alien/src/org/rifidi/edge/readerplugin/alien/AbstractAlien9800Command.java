/**
 * 
 */
package org.rifidi.edge.readerplugin.alien;

import org.rifidi.edge.core.sensors.commands.Command;

/**
 * A superclass for use by commands for the Alien9800 Reader
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractAlien9800Command extends Command {

	/**
	 * Default Constructor
	 * 
	 * @param commandID
	 *            The FACTORY_ID of the command.
	 */
	public AbstractAlien9800Command(String commandID) {
		super(commandID);
	}
}
