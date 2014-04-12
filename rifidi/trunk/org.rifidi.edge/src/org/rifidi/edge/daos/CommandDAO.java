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
