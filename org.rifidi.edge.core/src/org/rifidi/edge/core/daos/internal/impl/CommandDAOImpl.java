package org.rifidi.edge.core.daos.internal.impl;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.services.notification.NotifierService;

/**
 * Implementation of Command Data Access Object. Helps access objects used for
 * managing command configurations
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class CommandDAOImpl implements CommandDAO {

	/** The available Command Configuration factory factories */
	private Set<AbstractCommandConfigurationFactory<?>> commandFactories;
	/** The available Command Configuration Factories */
	private Map<String, AbstractCommandConfiguration<?>> commands;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(CommandDAOImpl.class);
	/** A notifier for JMS. Remove once we have aspects */
	private NotifierService notifierService;

	/**
	 * 
	 */
	public CommandDAOImpl() {
		commandFactories = new HashSet<AbstractCommandConfigurationFactory<?>>();
		commands = new HashMap<String, AbstractCommandConfiguration<?>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.daos.CommandDAO#getCommandByID(java.lang.String)
	 */
	@Override
	public AbstractCommandConfiguration<?> getCommandByID(String id) {
		return this.commands.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.daos.CommandDAO#getCommandFactories()
	 */
	@Override
	public Set<AbstractCommandConfigurationFactory<?>> getCommandFactories() {
		return new HashSet<AbstractCommandConfigurationFactory<?>>(
				commandFactories);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.daos.CommandDAO#getCommandFactoryByID(java.lang.
	 * String)
	 */
	@Override
	public AbstractCommandConfigurationFactory<?> getCommandFactoryByID(
			String id) {
		for (AbstractCommandConfigurationFactory<?> factory : commandFactories) {
			if (id.equals(factory.getFactoryID())) {
				return factory;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.daos.CommandDAO#getCommandFactoryByReaderID(java
	 * .lang.String)
	 */
	@Override
	public AbstractCommandConfigurationFactory<?> getCommandFactoryByReaderID(
			String id) {
		for (AbstractCommandConfigurationFactory<?> factory : commandFactories) {
			if (factory.getReaderFactoryID().equals(id)) {
				return factory;
			}
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.daos.CommandDAO#getCommands()
	 */
	@Override
	public Set<AbstractCommandConfiguration<?>> getCommands() {
		return new HashSet<AbstractCommandConfiguration<?>>(commands.values());
	}

	/**
	 * Used by spring to bind a new CommandConfigurationto this service.
	 * 
	 * @param commandConfiguration
	 *            the configuration to bind
	 * @param parameters
	 */
	public void bindCommand(
			AbstractCommandConfiguration<?> commandConfiguration,
			Dictionary<String, String> parameters) {
		logger.info("Command Configuration Bound: "
				+ commandConfiguration.getID());
		commands.put(commandConfiguration.getID(), commandConfiguration);
		notifierService.addCommandEvent(commandConfiguration.getID());
	}

	/**
	 * Used by spring to unbind a disappearing Command Configuration service
	 * from this service.
	 * 
	 * @param commandConfiguration
	 *            the AbstractCommandConfiguration to unbind
	 * @param parameters
	 */
	public void unbindCommand(
			AbstractCommandConfiguration<?> commandConfiguration,
			Dictionary<String, String> parameters) {
		logger.info("Command Configuration Unbound: "
				+ commandConfiguration.getID());
		commands.remove(commandConfiguration.getID());
		notifierService.removeCommandEvent(commandConfiguration.getID());
	}

	/**
	 * Used by spring to give the initial list of command configuration
	 * factories.
	 * 
	 * @param configurations
	 *            the initial list of available command configuration factories
	 */
	public void setCommands(Set<AbstractCommandConfiguration<?>> configurations) {
		for (AbstractCommandConfiguration<?> configuration : configurations) {
			this.commands.put(configuration.getID(), configuration);
		}
	}

	/**
	 * Used by spring to bind a new CommandConfigurationFactory to this service.
	 * 
	 * @param commandConfigurationFactory
	 *            the factory to bind
	 * @param parameters
	 */
	public void bindCommandFactory(
			AbstractCommandConfigurationFactory<?> commandConfigurationFactory,
			Dictionary<String, String> parameters) {
		logger.info("Command Configuration Factory Bound: "
				+ commandConfigurationFactory.getFactoryID());
		commandFactories.add(commandConfigurationFactory);

		// TODO: Remove once we have aspects
		if (notifierService != null) {
			notifierService
					.addCommandConfigFactoryEvent(commandConfigurationFactory
							.getReaderFactoryID());
		}
	}

	/**
	 * Used by spring to unbind a disappearing CommandConfigurationFactory
	 * service from this service.
	 * 
	 * @param commandConfigurationFactory
	 *            the CommandConfigurationFactory to unbind
	 * @param parameters
	 */
	public void unbindCommandFactory(
			AbstractCommandConfigurationFactory<?> commandConfigurationFactory,
			Dictionary<String, String> parameters) {
		logger.info("Command Configuration Factory Unbound: "
				+ commandConfigurationFactory.getFactoryID());
		commandFactories.remove(commandConfigurationFactory);

		// TODO: Remove once we have aspects
		if (notifierService != null) {
			notifierService
					.removeCommandConfigFactoryEvent(commandConfigurationFactory
							.getReaderFactoryID());
		}
	}

	/**
	 * Used by spring to give the initial list of command configuration
	 * factories.
	 * 
	 * @param factories
	 *            the initial list of available command configuration factories
	 */
	public void setCommandFactories(
			Set<AbstractCommandConfigurationFactory<?>> factories) {
		commandFactories.addAll(factories);
	}

	/**
	 * Called by Spring
	 * 
	 * @param notifierService
	 *            the notifierService to set
	 */
	public void setNotifierService(NotifierService notifierService) {
		this.notifierService = notifierService;
	}
}
