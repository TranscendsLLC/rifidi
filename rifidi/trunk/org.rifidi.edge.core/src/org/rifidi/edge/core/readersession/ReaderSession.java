package org.rifidi.edge.core.readersession;

import org.rifidi.edge.core.api.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
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
 * @author Kyle Neumeier - kyle@pramari.com
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
	public long executeCommand(CommandConfiguration configuration)
			throws RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException;

	/**
	 * Execute a series of synchronous properties
	 * 
	 * @param propertiesToExecute
	 *            the commands and their arguments
	 * @param set
	 *            if true, set the property, if false, get the current value of
	 *            the property
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
	public Document executeProperty(Document propertiesToExecute, boolean set)
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
	public void stopCurCommand(boolean force);

	/**
	 * Stop the currently executing command if it has the specified id
	 * 
	 * @param force
	 *            force the command to stop
	 * @param commandID
	 *            the id of command to stop
	 * @return true if command could be stopped
	 */
	public void stopCurCommand(boolean force, long commandID);

	/**
	 * Get the current executing command
	 * 
	 * @return the name of the current executing command null if there is none
	 */
	public String curExecutingCommand();

	/**
	 * Get the command name with the given command ID
	 * 
	 * @param id
	 *            An id of a currently executing or previously executed command
	 * @return The command name
	 */
	public String commandName(long id);

	/**
	 * Get the id of the current executing command
	 * 
	 * @return id of the current command, 0 if there is none executing
	 */
	public long curExecutingCommandID();

	/**
	 * Move the session from the Configured state to the OK state
	 */
	public void enable();

	/**
	 * Move the sesssion to the Configured state
	 */
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
	 * @return status of the current executing command, NONE if there is no
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
	 * Move the session from the Error state to the configured state
	 */
	public void resetReaderSession();

	/**
	 * Get the status of this reader session
	 * 
	 * @See ReaderSessionStatus
	 * @return The status of the reader
	 */
	public ReaderSessionStatus getStatus();

	/**
	 * The ReaderInfo can be changed when the session is in the configured state
	 * 
	 * @param readerInfo
	 *            The new reader info for this session
	 * @return true if successful
	 */
	public boolean setReaderInfo(ReaderInfo readerInfo);

	/**
	 * Get the ID of this reader session
	 * 
	 * @return The ID of this session
	 */
	public long getSessionID();
}
