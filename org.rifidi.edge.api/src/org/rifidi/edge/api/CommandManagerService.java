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
package org.rifidi.edge.api;

import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;


/**
 * This is the interface for a stub that allows clients to interact with
 * commands on the edge server.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface CommandManagerService {

	/**
	 * This method gets the available command types on the edge server and the
	 * ID of the ReaderFactory that the command type works with.
	 * 
	 * @return A set of CommandConfigPluginDTOs
	 */
	Set<CommandConfigFactoryDTO> getCommandConfigFactories();

	/**
	 * Get all the CommandConfigFactoryID associated with a readerFactoryID.
	 * 
	 * @param readerFactoryID
	 * @return
	 */
	Set<CommandConfigFactoryDTO> getCommandConfigFactoriesByReaderID(String readerFactoryID);

	/**
	 * Get the CommandConfigurationFactory
	 * @param commandFactoryID
	 * @return
	 */
	CommandConfigFactoryDTO getCommandConfigFactory(
			String commandFactoryID);

	/**
	 * Gets the DTOs for configured commands.
	 * 
	 * @return a set of configured commands
	 */
	Set<CommandConfigurationDTO> getCommands();

	/**
	 * Gets the DTO for a given Command Configuration.
	 * 
	 * @param commandConfigurationID
	 *            The ID of the commandConfiguration to get
	 * @return A DTO for the configured command, or null if no command
	 *         configuration is available for the given ID
	 */
	CommandConfigurationDTO getCommandConfiguration(
			String commandConfigurationID);

	/**
	 * Gets the meta information necessary to construct a new Command.
	 * 
	 * @param commandType
	 *            the type of command to make
	 * @return an MBeanInfo object that describes how to make a new command
	 */
	MBeanInfo getCommandDescription(String commandType);

	/**
	 * Create a new Command.
	 * 
	 * @param commandType
	 *            The type of the Command to make
	 * @param properties
	 *            the properties of a Command
	 * @return the id of new created command
	 */
	String createCommand(String commandType, AttributeList properties);

	/**
	 * Sets the properties of a Command.
	 * 
	 * @param commandID
	 *            the ID of the command to set
	 * @param properties
	 *            the new properties of the command
	 */
	void setCommandProperties(String commandID, AttributeList properties);

	/**
	 * Delete a command configuration.
	 * 
	 * @param commandConfigurationID
	 *            the ID of the commandConfiguration to delete
	 */
	void deleteCommand(String commandID);
}
