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
import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.internal.CommandConfigurationDAO;
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
	/** A data access object for the command configuration services */
	private CommandConfigurationDAO commandConfigDAO;
	/** A data access object for the readerSession configuration services */
	private ReaderDAO readerConfigDAO;
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
	public void setReaderDAO(ReaderDAO readerConfigDAO) {
		this.readerConfigDAO = readerConfigDAO;
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
