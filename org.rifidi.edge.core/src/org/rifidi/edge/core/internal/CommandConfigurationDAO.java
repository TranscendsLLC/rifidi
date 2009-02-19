/**
 * 
 */
package org.rifidi.edge.core.internal;

import java.util.Set;

import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.commands.CommandFactory;

/**
 * A Data Access Object that keeps track of CommandConfiguraiton services and
 * CommandConfigurationFactory services in the OSGi service registry
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface CommandConfigurationDAO {

	/**
	 * @param commandConfigurationFactoryFactoryID
	 * @return the CommandConfigurationFactory service with the given ID
	 */
	public AbstractCommandConfigurationFactory getCommandConfigurationFactoryFactory(
			String commandConfigurationFactoryFactoryID);

	/**
	 * @param commandConfigurationFactoryFactoryID
	 * @return the CommandConfigurationFactory service which provides a
	 *         commandConfigurationFactory service with the givenID
	 */
	public AbstractCommandConfigurationFactory getCommandConfigurationFactoryFactoryFromConfigFactoryID(
			String commandConfigurationFactoryID);

	/**
	 * @return all current CommandConfigurationFactoryFactory services
	 */
	public Set<AbstractCommandConfigurationFactory> getCurrentCommandConfigurationFactoryFactories();

	/**
	 * 
	 * @param commandConfigurationFactory
	 *            ID
	 * @return The CommandConfigurationFactory with the givenID or null if it
	 *         does not exist
	 */
	public CommandFactory<?> getCommandConfigurationFactory(
			String commandConfigurationFactoryID);

}
