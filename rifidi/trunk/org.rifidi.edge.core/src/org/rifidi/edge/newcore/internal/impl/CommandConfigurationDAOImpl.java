/**
 * 
 */
package org.rifidi.edge.newcore.internal.impl;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.rifidi.configuration.Configuration;
import org.rifidi.edge.newcore.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.newcore.commands.CommandConfiguration;
import org.rifidi.edge.newcore.internal.CommandConfigurationDAO;

/**
 * @author kyle
 * 
 */
public class CommandConfigurationDAOImpl implements CommandConfigurationDAO {

	/** The available Command Configuration factories */
	private Set<AbstractCommandConfigurationFactory> commandConfigFactories;

	public CommandConfigurationDAOImpl() {
		commandConfigFactories = new HashSet<AbstractCommandConfigurationFactory>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.internal.CommandConfigurationDAO#
	 * getCommandConfigurationFactory(java.lang.String)
	 */
	@Override
	public AbstractCommandConfigurationFactory getCommandConfigurationFactory(
			String commandConfigurationFactoryID) {
		Iterator<AbstractCommandConfigurationFactory> iter = commandConfigFactories
				.iterator();
		AbstractCommandConfigurationFactory current = null;
		while (iter.hasNext()) {
			current = iter.next();
			if (current.getID().equals(commandConfigurationFactoryID)) {
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
	public Set<AbstractCommandConfigurationFactory> getCurrentCommandConfigurationFactories() {
		return new HashSet<AbstractCommandConfigurationFactory>(
				this.commandConfigFactories);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.internal.CommandConfigurationDAO#getCommandFactory
	 * (java.lang.String)
	 */
	@Override
	public Configuration getDefaultCommandConfiguration(
			String commandFactoryID) {
		Iterator<AbstractCommandConfigurationFactory> iter = commandConfigFactories
				.iterator();
		AbstractCommandConfigurationFactory current = null;
		while (iter.hasNext()) {
			current = iter.next();
			for (String s : current.getFactoryIDs()) {
				if (s.equals(commandFactoryID)) {
					return current.getEmptyConfiguration(commandFactoryID);
				}
			}
		}
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
