
package org.rifidi.edge.core.daos;

import java.util.Set;

import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
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
	 * @param id	The ID of the desired command.  
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
	 * @param id	The ID of the desired factory.  
	 * @return
	 */
	AbstractCommandConfigurationFactory getCommandFactoryByID(String id);
	
	/**
	 * Get a command factory by the reader ID that it is associated with.
	 * 
	 * 
	 * @param id The ID of the ReaderFactory
	 * @return The CommandConfigurationFactory
	 */
	AbstractCommandConfigurationFactory getCommandFactoryByReaderID(String id);

}
