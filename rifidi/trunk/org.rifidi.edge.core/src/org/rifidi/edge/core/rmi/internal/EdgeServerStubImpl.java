/**
 * 
 */
package org.rifidi.edge.core.rmi.internal;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.services.ConfigurationService;
import org.rifidi.edge.core.exceptions.NonExistentCommandFactoryException;
import org.rifidi.edge.core.exceptions.NonExistentReaderConfigurationException;
import org.rifidi.edge.core.internal.CommandConfigurationDAO;
import org.rifidi.edge.core.internal.ReaderConfigurationDAO;
import org.rifidi.edge.core.readersession.ReaderSessionDAO;
import org.rifidi.edge.core.rmi.EdgeServerStub;

/**
 * This is the implementation for the Edge Server RMI Stub
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerStubImpl implements EdgeServerStub {

	/** The configuration service for the edge server */
	private ConfigurationService configurationService;
	/** The object that manages creating and starting readerSessions */
	private ReaderSessionDAO readerSessionDAO;
	/** A data access object for the command configuration services */
	private CommandConfigurationDAO commandConfigDAO;
	/** A data access object for the reader configuration services */
	private ReaderConfigurationDAO readerConfigDAO;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(EdgeServerStubImpl.class);

	@Override
	public void save() throws RemoteException {
		configurationService.storeConfiguration();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.EdgeServerStub#getReaderSessions()
	 */
	@Override
	public Map<String, List<String>> getReaderSessions() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#setSessionReaderConfiguration
	 * (java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean setSessionReaderConfiguration(String readerSessionID,
			String readerConfigurationID, String commandConfigurationID)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.EdgeServerStub#startReaderSession(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public String startReaderSession(String readerConfigurationName,
			String commandFactoryName) throws RemoteException, NonExistentCommandFactoryException, NonExistentReaderConfigurationException {
		return readerSessionDAO.createAndStartReaderSession(readerConfigurationName,
				commandFactoryName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.EdgeServerStub#stopReaderSession(java.lang
	 * .String)
	 */
	@Override
	public void stopReaderSession(String readerSessionName)
			throws RemoteException {
		readerSessionDAO.stopReaderSession(readerSessionName);
	}

	/**
	 * Used by spring to set the CommandConfigurationDAO
	 * 
	 * @param commandConfigDAO
	 *            the commandConfigDAO to set
	 */
	public void setCommandConfigDAO(CommandConfigurationDAO commandConfigDAO) {
		this.commandConfigDAO = commandConfigDAO;
	}

	/**
	 * Used by spring to set the ReaderConfigDAO
	 * 
	 * @param readerConfigDAO
	 *            the readerConfigDAO to set
	 */
	public void setReaderConfigDAO(ReaderConfigurationDAO readerConfigDAO) {
		this.readerConfigDAO = readerConfigDAO;
	}

	/**
	 * @param readerSessionManagement
	 *            the readerSessionManagement to set
	 */
	public void setReaderSessionDAO(ReaderSessionDAO readerSessionManagement) {
		this.readerSessionDAO = readerSessionManagement;
	}

	/**
	 * @param configurationService
	 *            the configurationService to set
	 */
	public void setConfigurationService(
			ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}

}
