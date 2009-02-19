/**
 * 
 */
package org.rifidi.edge.core.internal.impl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.commands.CommandFactory;
import org.rifidi.edge.core.internal.CommandConfigurationDAO;

/**
 * @author kyle
 * 
 */
public class CommandConfigurationDAOImpl implements CommandConfigurationDAO {

	/** The available Command Configuration factory factories */
	private Set<AbstractCommandConfigurationFactory> commandConfigFactoryFactories;
	/** The available Command Configuration Factories */
	private Set<CommandFactory<?>> commandConfigFactories;

	public CommandConfigurationDAOImpl() {
		commandConfigFactoryFactories = new HashSet<AbstractCommandConfigurationFactory>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.internal.CommandConfigurationDAO#
	 * getCommandConfigurationFactory(java.lang.String)
	 */
	@Override
	public AbstractCommandConfigurationFactory getCommandConfigurationFactoryFactory(
			String commandConfigurationFactoryFactoryID) {
		Iterator<AbstractCommandConfigurationFactory> iter = commandConfigFactoryFactories
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
	public AbstractCommandConfigurationFactory getCommandConfigurationFactoryFactoryFromConfigFactoryID(
			String commandConfigurationFactoryID) {
		Iterator<AbstractCommandConfigurationFactory> iter = commandConfigFactoryFactories
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
	public Set<AbstractCommandConfigurationFactory> getCurrentCommandConfigurationFactoryFactories() {
		return new HashSet<AbstractCommandConfigurationFactory>(
				this.commandConfigFactoryFactories);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.internal.CommandConfigurationDAO#
	 * getCommandConfigurationFactory(java.lang.String)
	 */
	@Override
	public CommandFactory<?> getCommandConfigurationFactory(
			String commandConfigurationFactoryID) {
		return null;
	}

	/**
	 * Used by spring to bind a new CommandConfigurationFactory to this service.
	 * 
	 * @param commandConfigurationFactory
	 *            the factory to bind
	 * @param parameters
	 */
	public void bindCommandConfigFactory(
			CommandFactory<?> commandConfigurationFactory,
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
			CommandFactory<?> commandConfigurationFactory,
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
	public void setCommandConfigFactories(Set<CommandFactory<?>> factories) {
		commandConfigFactories.addAll(factories);
	}

	/**
	 * Used by spring to bind a new CommandConfigurationFactory to this service.
	 * 
	 * @param commandConfigurationFactory
	 *            the factory to bind
	 * @param parameters
	 */
	public void bindCommandConfigFactoryFactory(
			AbstractCommandConfigurationFactory commandConfigurationFactory,
			Dictionary<String, String> parameters) {
		commandConfigFactoryFactories.add(commandConfigurationFactory);
	}

	/**
	 * Used by spring to unbind a disappearing CommandConfigurationFactory
	 * service from this service.
	 * 
	 * @param commandConfigurationFactory
	 *            the CommandConfigurationFactory to unbind
	 * @param parameters
	 */
	public void unbindCommandConfigFactoryFactory(
			AbstractCommandConfigurationFactory commandConfigurationFactory,
			Dictionary<String, String> parameters) {
		commandConfigFactoryFactories.remove(commandConfigurationFactory);
	}

	/**
	 * Used by spring to give the initial list of command configuration
	 * factories.
	 * 
	 * @param factories
	 *            the initial list of available command configuration factories
	 */
	public void setCommandConfigFactoryFactories(
			Set<AbstractCommandConfigurationFactory> factories) {
		commandConfigFactoryFactories.addAll(factories);
	}

}
