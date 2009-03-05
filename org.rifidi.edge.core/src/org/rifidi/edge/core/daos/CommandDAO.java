/**
 * 
 */
package org.rifidi.edge.core.daos;

import java.util.Set;

import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface CommandDAO {
	/**
	 * Get commands currently created.
	 * 
	 * @return
	 */
	Set<AbstractCommandConfiguration<?>> getCommands();

	/**
	 * Get a command by its ID.
	 * 
	 * @param id
	 * @return
	 */
	AbstractCommandConfiguration<?> getCommandByID(String id);

	/**
	 * Get currently available command factories.
	 * 
	 * @return
	 */
	Set<AbstractCommandConfigurationFactory> getCommandFactories();

	/**
	 * Get a command factory by its id.
	 * 
	 * @param id
	 * @return
	 */
	AbstractCommandConfigurationFactory getCommandFactoryByID(String id);

}
