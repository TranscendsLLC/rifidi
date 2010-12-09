/*
 * CommandManagerService.java
 * 
 * Created:     July 22nd, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:     The software in this package is published under the terms of the EPL License
 *                   A copy of the license is included in this distribution under Rifidi-License.txt 
 */
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
	 */
	void createCommand(String commandType, AttributeList properties);

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
