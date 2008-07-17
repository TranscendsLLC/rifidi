package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;

/**
 * Implementation for a RemoteReaderConnection
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoteReaderConnectionImpl implements RemoteReaderConnection {

	private Log logger = LogFactory.getLog(RemoteReaderConnectionImpl.class);

	/**
	 * Internal ReaderSession which gets exposed with this
	 * RemoteReaderConnection
	 */
	private ReaderSession readerSession;

	/**
	 * Create a new RemoteReaderConnection associated with the ReaderSession
	 * 
	 * @param readerSession
	 *            with which the RemoteReaderConnection is assciated
	 */
	public RemoteReaderConnectionImpl(ReaderSession readerSession) {
		this.readerSession = readerSession;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#commandStatus(long)
	 */
	@Override
	public String commandStatus(long id) {
		return readerSession.commandStatus(id).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#commandStatus()
	 */
	@Override
	public String commandStatus() {
		return readerSession.commandStatus().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#curExecutingCommand()
	 */
	@Override
	public String curExecutingCommand() throws RemoteException {
		return readerSession.curExecutingCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#curExecutingCommandID()
	 */
	@Override
	public long curExecutingCommandID() throws RemoteException {
		return readerSession.curExecutingCommandID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#executeCommand(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public long executeCommand(String command, String configuration)
			throws RemoteException {
		try {
			return readerSession.executeCommand(command, configuration);
		} catch (RifidiConnectionException e) {
			logger.error("RifidiConnectionException", e);
			throw new RemoteException("RifidiConnectionException", e);
		} catch (RifidiCommandInterruptedException e) {
			logger.error("RifidiCommandInterruptedException", e);
			throw new RemoteException("RifidiCommandInterruptedException", e);
		} catch (RifidiCommandNotFoundException e) {
			logger.error("RifidiCommandNotFoundException", e);
			throw new RemoteException("RifidiCommandNotFoundException", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#getAvailableCommandGroups()
	 */
	@Override
	public List<String> getAvailableCommandGroups() throws RemoteException {
		return readerSession.getAvailableCommandGroups();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#getAvailableCommands()
	 */
	@Override
	public List<String> getAvailableCommands() throws RemoteException {
		return readerSession.getAvailableCommands();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#getAvailableCommands(java.lang.String)
	 */
	@Override
	public List<String> getAvailableCommands(String groupName)
			throws RemoteException {
		return readerSession.getAvailableCommands(groupName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#getMessageQueueName()
	 */
	@Override
	public String getMessageQueueName() throws RemoteException {
		return readerSession.getMessageQueueName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#getReaderInfo()
	 */
	@Override
	public ReaderInfo getReaderInfo() throws RemoteException {
		return readerSession.getReaderInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#getReaderState()
	 */
	@Override
	public String getReaderState() throws RemoteException {
		return readerSession.getStatus().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#resetReaderConnection()
	 */
	@Override
	public void resetReaderConnection() throws RemoteException {
		readerSession.resetReaderSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#startTagStream(java.lang.String)
	 */
	@Override
	public long startTagStream(String configuration) throws RemoteException {
		try {
			return readerSession.executeCommand("TagStreaming", configuration);
		} catch (RifidiConnectionException e) {
			logger.error("RifidiConnectionException", e);
			throw new RemoteException("RifidiConnectionException", e);
		} catch (RifidiCommandInterruptedException e) {
			logger.error("RifidiCommandInterruptedException", e);
			throw new RemoteException("RifidiCommandInterruptedException", e);
		} catch (RifidiCommandNotFoundException e) {
			// TODO this might happen more often
			// logger.error("RifidiCommandNotFoundException", e);
			logger.error("Command not found" + e.getMessage());
			throw new RemoteException("RifidiCommandNotFoundException", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#stopCurCommand(boolean)
	 */
	@Override
	public boolean stopCurCommand(boolean force) throws RemoteException {
		return readerSession.stopCurCommand(force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#stopCurCommand(boolean,
	 *      long)
	 */
	@Override
	public boolean stopCurCommand(boolean force, long commandID)
			throws RemoteException {
		return readerSession.stopCurCommand(force, commandID);
	}

	/**
	 * Get the internal ReaderSession this RemoteReaderConnection is associated
	 * with. This should only be used to delete the ReaderSession if this
	 * RemoteReaderSession gets deleted, too.
	 * 
	 * @return the internal ReaderSession
	 */
	public ReaderSession getReaderSession() {
		return readerSession;
	}

}
