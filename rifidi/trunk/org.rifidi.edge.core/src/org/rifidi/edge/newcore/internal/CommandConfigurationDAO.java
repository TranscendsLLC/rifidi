/**
 * 
 */
package org.rifidi.edge.newcore.internal;

import java.util.Set;

import org.rifidi.configuration.Configuration;
import org.rifidi.edge.newcore.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.newcore.commands.CommandConfiguration;

/**
 * A Data Access Object that keeps track of CommandConfiguraiton services and
 * CommandConfigurationFactory services in the OSGi service registry
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface CommandConfigurationDAO {

	/**
	 * @param commandConfigurationFactoryID
	 * @return the CommandConfigurationFactory service with the given ID
	 */
	public AbstractCommandConfigurationFactory getCommandConfigurationFactory(
			String commandConfigurationFactoryID);

	/**
	 * @return all current CommandConfigurationFactory services
	 */
	public Set<AbstractCommandConfigurationFactory> getCurrentCommandConfigurationFactories();

	/**
	 * @param commandFactoryID
	 *            the ID of the commandFactory
	 * @return a default Command Configuration for the given ID
	 */
	public Configuration getDefaultCommandConfiguration(
			String commandFactoryID);

}
