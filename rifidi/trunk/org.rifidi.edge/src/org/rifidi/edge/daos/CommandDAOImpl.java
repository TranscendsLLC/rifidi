/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.daos;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.notification.NotifierService;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

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
	 * org.rifidi.edge.daos.CommandDAO#getCommandByID(java.lang.String)
	 */
	@Override
	public AbstractCommandConfiguration<?> getCommandByID(String id) {
		return this.commands.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.daos.CommandDAO#getCommandFactories()
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
	 * org.rifidi.edge.daos.CommandDAO#getCommandFactoryByID(java.lang.
	 * String)
	 */
	@Override
	public AbstractCommandConfigurationFactory<?> getCommandFactory(String id) {
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
	 * org.rifidi.edge.daos.CommandDAO#getCommandFactoryByReaderID(java
	 * .lang.String)
	 */
	@Override
	public Set<AbstractCommandConfigurationFactory<?>> getCommandFactoryByReaderID(
			String id) {
		Set<AbstractCommandConfigurationFactory<?>> factories = new HashSet<AbstractCommandConfigurationFactory<?>>();
		for (AbstractCommandConfigurationFactory<?> factory : commandFactories) {
			if (factory.getReaderFactoryID().equals(id)) {
				factories.add(factory);
			}
		}
		return factories;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.daos.CommandDAO#getCommands()
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
		logger.debug("Command Configuration Factory Bound: "
				+ commandConfigurationFactory.getFactoryID());
		commandFactories.add(commandConfigurationFactory);

		// TODO: Remove once we have aspects
		if (notifierService != null) {
			notifierService.addCommandConfigFactoryEvent(
					commandConfigurationFactory.getReaderFactoryID(),
					commandConfigurationFactory.getFactoryID());
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
			notifierService.removeCommandConfigFactoryEvent(
					commandConfigurationFactory.getReaderFactoryID(),
					commandConfigurationFactory.getFactoryID());
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
