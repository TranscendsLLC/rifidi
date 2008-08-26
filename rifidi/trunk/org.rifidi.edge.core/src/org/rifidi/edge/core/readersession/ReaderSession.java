package org.rifidi.edge.core.readersession;

import java.util.Collection;

import org.rifidi.edge.core.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;
import org.w3c.dom.Document;

/**
 * The ReaderSession is a instance of a Session to a specific Reader. It allows
 * to control the execution of commands of the reader, as well the streaming of
 * tags. It's a capable to notify about the current state of the reader and the
 * state of commands executed on it. It's creating a communication, messageQueue
 * and Execution environment of any type of ReaderPlugin. It listens to
 * communication events.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public interface ReaderSession {

	// TODO: Need a way to tell what exceptions cause a restart and ones that do
	// not.
	/**
	 * Execute an asynchronous command
	 * 
	 * @param configuration
	 *            the command and configuration passed in as a argument into the
	 *            command
	 * @return the id assigned command
	 * @throws RifidiConnectionException
	 *             if the connection failed
	 * @throws RifidiCommandInterruptedException
	 *             if the command could not be started or was interrupted
	 * @throws RifidiCommandNotFoundException
	 *             the given command could not be found
	 * @throws RifidiInvalidConfigurationException
	 */
	public long executeCommand(Document configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException,
			RifidiInvalidConfigurationException;

	/**
	 * Execute a series of synchronous properties
	 * 
	 * @param propertiesToExecute
	 *            the commands and their arguments
	 * @return An XML as a string that contains the results of all the
	 *         properties executed
	 * @throws RifidiConnectionException
	 *             if the connection failed
	 * @throws RifidiCommandNotFoundException
	 *             the given command could not be found
	 * @throws RifidiCommandInterruptedException
	 *             if the command could not be started or was interrupted
	 * @throws RifidiInvalidConfigurationException
	 * @throws RifidiCannotRestartCommandException
	 */
	public Document executeProperty(Document propertiesToExecute)
			throws RifidiConnectionException, RifidiCommandNotFoundException,
			RifidiCommandInterruptedException,
			RifidiInvalidConfigurationException,
			RifidiCannotRestartCommandException;

	/**
	 * Stop the currently executing command
	 * 
	 * @param force
	 *            force the command to stop
	 * @return true if command could be stopped
	 */
	public boolean stopCurCommand(boolean force);

	/**
	 * Stop the currently executing command if it has the specified id
	 * 
	 * @param force
	 *            force the command to stop
	 * @param commandID
	 *            the id of command to stop
	 * @return true if command could be stopped
	 */
	public boolean stopCurCommand(boolean force, long commandID);

	/**
	 * Get the current executing command
	 * 
	 * @return the name of the current executing command null if there is none
	 */
	public String curExecutingCommand();

	/**
	 * Get the id of the current executing command
	 * 
	 * @return id of the current command, 0 if there is none executing
	 */
	public long curExecutingCommandID();

	public void enable();

	public void disable();

	/**
	 * Get the status of the command with the specified id
	 * 
	 * @param id
	 *            id of the command
	 * @return the status of the command
	 */
	public CommandStatus commandStatus(long id);

	/**
	 * Get the status of the currently executing command
	 * 
	 * @return status of the current executing command, NONE if ther is no
	 *         command executing
	 */
	public CommandStatus commandStatus();

	/**
	 * Get the name of the MessageQueue associated with this ReaderSession
	 * 
	 * @return the name of the MessageQueue
	 */
	public String getMessageQueueName();

	/**
	 * Get the ReaderInfo of this ReaderSession
	 * 
	 * @return the ReaderInfo associated with this ReaderSession
	 */
	public ReaderInfo getReaderInfo();

	/**
	 * Restart the ReaderSession to reset a Error State
	 */
	public void resetReaderSession();

	/**
	 * Get all available command groups
	 * 
	 * @return a list of groups
	 */
	public Collection<String> getAvailableCommandGroups();

	/**
	 * Get all available property groups
	 * 
	 * @return a list of groups
	 */
	public Collection<String> getAvailablePropertyGroups();

	/**
	 * Get all commands in the specified group
	 * 
	 * @param groupName
	 *            the group to look for the commands
	 * @return a list of commands in the group
	 */
	public Collection<String> getCommandsForGroup(String groupName);

	/**
	 * Get all properties in the specified group
	 * 
	 * @param groupName
	 *            the group to look for the properties
	 * @return a list of properties in the group
	 */
	public Collection<String> getPropertiesForGroup(String groupName);

	public Collection<String> getAvailableCommands();

	public Collection<String> getAvailableProperties();

	public ReaderSessionStatus getStatus();

	public boolean setReaderInfo(ReaderInfo readerInfo);
}
