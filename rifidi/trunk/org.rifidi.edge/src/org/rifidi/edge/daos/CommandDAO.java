/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.daos;

import java.util.Set;

import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * Interface for Data Access Object that helps access objects for managing
 * command configurations
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
	 * @param id
	 *            The ID of the desired command.
	 * @return
	 */
	AbstractCommandConfiguration<?> getCommandByID(String id);

	/**
	 * Get currently available command factories.
	 * 
	 * @return
	 */
	Set<AbstractCommandConfigurationFactory<?>> getCommandFactories();

	/**
	 * Get a command factory by its id.
	 * 
	 * @param id
	 *            The ID of the desired factory.
	 * @return
	 */
	AbstractCommandConfigurationFactory<?> getCommandFactory(String id);

	/**
	 * Get a command factory by the reader ID that it is associated with.
	 * 
	 * 
	 * @param id
	 *            The ID of the ReaderFactory
	 * @return The CommandConfigurationFactory
	 */
	Set<AbstractCommandConfigurationFactory<?>> getCommandFactoryByReaderID(String id);

}
