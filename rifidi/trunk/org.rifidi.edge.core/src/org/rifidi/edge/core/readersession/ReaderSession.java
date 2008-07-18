package org.rifidi.edge.core.readersession;

import java.util.List;

import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.impl.enums.CommandStatus;
import org.rifidi.edge.core.readersession.impl.enums.ReaderSessionStatus;

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

	/**
	 * Get the current status of the ReaderSession
	 * 
	 * @return the status of the ReaderSession
	 */
	public ReaderSessionStatus getStatus();

	/**
	 * Get a list of available commands for this instance of a ReaderPlugin
	 * 
	 * @return a list of availbale commands
	 */
	public List<String> getAvailableCommands();

	/**
	 * Get a list of available commands in the named group
	 * 
	 * @param groupName
	 *            the name of the group to look up the commands
	 * @return the commands in the specified group
	 */
	public List<String> getAvailableCommands(String groupName);

	/**
	 * Get a list of available command groups of this instance of a ReaderPlugin
	 * 
	 * @return list of groups available in this ReaderPlugin
	 */
	public List<String> getAvailableCommandGroups();

	// TODO: Need a way to tell what exceptions cause a restart and ones that do
	// not.
	/**
	 * Execute a command
	 * 
	 * @param command
	 *            the command to execute
	 * @param configuration
	 *            the configuration passed in as a argument into the command
	 * @return the id assigned command
	 * @throws RifidiConnectionException
	 *             if the connection failed
	 * @throws RifidiCommandInterruptedException
	 *             if the command could not be started or was interrupted
	 * @throws RifidiCommandNotFoundException
	 *             the given command could not be found
	 */
	public long executeCommand(String command, String configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException;

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
}
