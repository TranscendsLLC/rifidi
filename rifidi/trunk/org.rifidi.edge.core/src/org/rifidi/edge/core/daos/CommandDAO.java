/*
 * 
 * CommandDAO.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
package org.rifidi.edge.core.daos;

import java.util.Set;

import org.rifidi.edge.core.configuration.impl.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.sensors.commands.AbstractCommandConfiguration;

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
