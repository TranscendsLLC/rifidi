/**
 * 
 */
package org.rifidi.edge.core.internal.impl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.internal.CommandConfigurationDAO;

/**
 * @author kyle
 * 
 */
public class CommandConfigurationDAOImpl implements CommandConfigurationDAO {

	/** The available Command Configuration factory factories */
	private Set<AbstractCommandConfigurationFactory> commandConfigFactories;
	/** The available Command Configuration Factories */
	private Set<AbstractCommandConfiguration<?>> abstractCommandConfigurations;

	public CommandConfigurationDAOImpl() {
		commandConfigFactories = new HashSet<AbstractCommandConfigurationFactory>();
		abstractCommandConfigurations = new HashSet<AbstractCommandConfiguration<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.internal.CommandConfigurationDAO#
	 * getCommandConfigurationFactory(java.lang.String)
	 */
	@Override
	public AbstractCommandConfigurationFactory getCommandConfigurationFactory(
			String commandConfigurationFactoryFactoryID) {
		Iterator<AbstractCommandConfigurationFactory> iter = commandConfigFactories
				.iterator();
		AbstractCommandConfigurationFactory current = null;
		while (iter.hasNext()) {
			current = iter.next();
			if (current.getID().equals(commandConfigurationFactoryFactoryID)) {
				return current;
			}
		}
		return null;
	}

	@Override
	public AbstractCommandConfigurationFactory getCommandConfigurationFactoryFromType(
			String commandConfigurationFactoryID) {
		Iterator<AbstractCommandConfigurationFactory> iter = commandConfigFactories
				.iterator();
		AbstractCommandConfigurationFactory current = null;
		while (iter.hasNext()) {
			current = iter.next();
			if (current.getFactoryIDs().contains(commandConfigurationFactoryID)) {
				return current;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.internal.CommandConfigurationDAO#
	 * getCurrentCommandConfigurationFactories(java.lang.String)
	 */
	@Override
	public Set<AbstractCommandConfigurationFactory> getCommandConfigurationFactories() {
		return new HashSet<AbstractCommandConfigurationFactory>(
				this.commandConfigFactories);
	}

	@Override
	public Set<String> getCommandConfigurationTypes(
			String commandConfigurationFactoryID) {
		Set<String> types = new HashSet<String>();
		Iterator<AbstractCommandConfigurationFactory> iter = commandConfigFactories
				.iterator();
		while (iter.hasNext()) {
			AbstractCommandConfigurationFactory factory = iter.next();
			types.addAll(factory.getFactoryIDs());
		}
		return types;
	}

	@Override
	public AbstractCommandConfiguration<?> getCommandConfiguration(
			String commandConfigID) {
		Iterator<AbstractCommandConfiguration<?>> iter = abstractCommandConfigurations
				.iterator();
		while(iter.hasNext()){
			AbstractCommandConfiguration<?> cc = iter.next();
			if(cc.getID().equals(commandConfigID)){
				return cc;
			}
		}
		return null;
	}

	@Override
	public Set<AbstractCommandConfiguration<?>> getCommandConfigurations() {
		return new HashSet<AbstractCommandConfiguration<?>>(this.abstractCommandConfigurations);
	}

	/**
	 * Used by spring to bind a new CommandConfigurationto this service.
	 * 
	 * @param commandConfiguration
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Dictionary<String, String> parameters) {
		abstractCommandConfigurations.add(commandConfiguration);
	}

	/**
	 * Used by spring to unbind a disappearing Command Configuration service
	 * from this service.
	 * 
	 * @param commandConfiguration
	 *            the AbstractCommandConfiguration to unbind
	 * @param parameters
	 */
	public void unbindCommandConfiguration(
			AbstractCommandConfiguration<?> commandConfiguration,
			Dictionary<String, String> parameters) {
		abstractCommandConfigurations.remove(commandConfiguration);
	}

	/**
	 * Used by spring to give the initial list of command configuration
	 * factories.
	 * 
	 * @param configurations
	 *            the initial list of available command configuration factories
	 */
	public void setCommandConfigurations(
			Set<AbstractCommandConfiguration<?>> configurations) {
		abstractCommandConfigurations.addAll(configurations);
	}

	/**
	 * Used by spring to bind a new CommandConfigurationFactory to this service.
	 * 
	 * @param commandConfigurationFactory
	 *            the factory to bind
	 * @param parameters
	 */
	public void bindCommandConfigFactory(
			AbstractCommandConfigurationFactory commandConfigurationFactory,
			Dictionary<String, String> parameters) {
		commandConfigFactories.add(commandConfigurationFactory);
	}

	/**
	 * Used by spring to unbind a disappearing CommandConfigurationFactory
	 * service from this service.
	 * 
	 * @param commandConfigurationFactory
	 *            the CommandConfigurationFactory to unbind
	 * @param parameters
	 */
	public void unbindCommandConfigFactory(
			AbstractCommandConfigurationFactory commandConfigurationFactory,
			Dictionary<String, String> parameters) {
		commandConfigFactories.remove(commandConfigurationFactory);
	}

	/**
	 * Used by spring to give the initial list of command configuration
	 * factories.
	 * 
	 * @param factories
	 *            the initial list of available command configuration factories
	 */
	public void setCommandConfigFactories(
			Set<AbstractCommandConfigurationFactory> factories) {
		commandConfigFactories.addAll(factories);
	}
}
