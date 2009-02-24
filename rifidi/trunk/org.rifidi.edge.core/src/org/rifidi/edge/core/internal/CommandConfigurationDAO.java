/**
 * 
 */
package org.rifidi.edge.core.internal;

import java.util.Set;

import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;

/**
 * A Data Access Object that keeps track of CommandConfiguraiton services and
 * CommandConfigurationFactory services in the OSGi service registry
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface CommandConfigurationDAO {

	Set<AbstractCommandConfigurationFactory> getCommandConfigurationFactories();

	/**
	 * Get the CommandConfigurationFactory with the givenID
	 * 
	 * @param commandConfigurationFactoryID
	 * @return
	 */
	AbstractCommandConfigurationFactory getCommandConfigurationFactory(
			String commandConfigurationFactoryID);

	/**
	 * Get the command configuration factory from the configuration type
	 * @param commandConfigurationType
	 * @return
	 */
	AbstractCommandConfigurationFactory getCommandConfigurationFactoryFromType(
			String commandConfigurationType);

	/**
	 * Get the types of a command configuration that a particular command
	 * configuraiton factory can produce
	 * 
	 * @param commandConfigurationFactoryID
	 * @return
	 */
	Set<String> getCommandConfigurationTypes(
			String commandConfigurationFactoryID);

	/**
	 * 
	 * @return the set of all currently configured commands
	 */
	Set<AbstractCommandConfiguration<?>> getCommandConfigurations();
	
	/**
	 * Get the CommandConfiguraiton associated with the given ID
	 * @param commandConfigID
	 * @return
	 */
	AbstractCommandConfiguration<?> getCommandConfiguration(String commandConfigID);

}
