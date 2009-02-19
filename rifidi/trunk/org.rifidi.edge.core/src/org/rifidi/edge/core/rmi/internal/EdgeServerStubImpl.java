/**
 * 
 */
package org.rifidi.edge.core.rmi.internal;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.internal.CommandConfigurationDAO;
import org.rifidi.edge.core.internal.ReaderConfigurationDAO;
import org.rifidi.edge.core.readers.AbstractReaderConfigurationFactory;
import org.rifidi.edge.core.readersession.ReaderSessionDAO;
import org.rifidi.edge.core.rmi.EdgeServerStub;

/**
 * This is the implementation for the Edge Server RMI Stub
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerStubImpl implements EdgeServerStub {

	/** The object that manages creating and starting readerSessions */
	private ReaderSessionDAO readerSessionDAO;
	/** A data access object for the command configuration services */
	private CommandConfigurationDAO commandConfigDAO;
	/** A data access object for the reader configuration services */
	private ReaderConfigurationDAO readerConfigDAO;
	/** The logger for this class */
	private static Log logger = LogFactory.getLog(EdgeServerStubImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#createCommandConfiguraion(
	 * java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public String createCommandConfiguraion(
			String commandConfigurationFactoryID,
			AttributeList commandConfigurationProperties)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#createReaderConfiguration(
	 * java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public String createReaderConfiguration(
			String readerConfigurationFactoryID,
			AttributeList readerConfigurationProperties) throws RemoteException {

		AbstractReaderConfigurationFactory<?> readerConfigFactory = this.readerConfigDAO
				.getReaderConfigurationFactory(readerConfigurationFactoryID);
		Configuration readerConfiguration = readerConfigFactory
				.getEmptyConfiguration(readerConfigurationFactoryID);
		readerConfiguration.setAttributes(readerConfigurationProperties);
		readerConfigFactory.createService(readerConfiguration);

		// TODO: How to get ID of configuration?
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#deleteCommandFactory(java.
	 * lang.String)
	 */
	@Override
	public void deleteCommandFactory(String commandConfigurationName)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#deleteReaderConfiguration(
	 * java.lang.String)
	 */
	@Override
	public void deleteReaderConfiguration(String readerConfigurationID)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.rmi.EdgeServerStub#
	 * getAllReaderConfigurationProperties()
	 */
	@Override
	public Map<String, AttributeList> getAllReaderConfigurationProperties()
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.rmi.EdgeServerStub#
	 * getAvailableCommandConfigurationFactories()
	 */
	@Override
	public Map<String, String> getAvailableCommandConfigurationFactories()
			throws RemoteException {
		// return set
		Map<String, String> retVal = new HashMap<String, String>();

		// step through each reader config factory
		Iterator<AbstractReaderConfigurationFactory<?>> readerConfigFacIter = readerConfigDAO
				.getCurrentReaderConfigurationFactories().iterator();
		while (readerConfigFacIter.hasNext()) {
			AbstractReaderConfigurationFactory<?> RCF = readerConfigFacIter
					.next();

			// get the CommandConfigurationFactoryFactory ID that is associated
			// with this reader config factory
			String commandConfigFacFacID = RCF
					.getCommandConfigurationFactoryFactoryID();

			AbstractCommandConfigurationFactory accff = commandConfigDAO
					.getCommandConfigurationFactoryFactory(commandConfigFacFacID);

			if (accff != null) {
				// add an entry in retval for each commandConfigurationFactory
				// in the CommandConfigurationFactoryFactory
				for (String ID : accff.getFactoryIDs()) {
					retVal.put(ID, RCF.getFactoryIDs().get(0));
				}
			} else {
				logger.warn("There is no "
						+ "CommandConfigurationFactoryFactory with ID: "
						+ commandConfigFacFacID);
			}
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#getAvailableCommandConfigurations
	 * (java.lang.String)
	 */
	@Override
	public Map<String, String> getAvailableCommandConfigurations(
			String readerConfigurationFactoryID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.newcore.rmi.EdgeServerStub#
	 * getAvailableReaderConfigurationFactories()
	 */
	@Override
	public Set<String> getAvailableReaderConfigurationFactories()
			throws RemoteException {

		Iterator<AbstractReaderConfigurationFactory<?>> iter = readerConfigDAO
				.getCurrentReaderConfigurationFactories().iterator();
		Set<String> retVal = new HashSet<String>();
		AbstractReaderConfigurationFactory<?> current = null;

		// step through each ReaderConfigurationFactory and add the ID to the
		// set
		while (iter.hasNext()) {
			current = iter.next();
			retVal.add(current.getFactoryIDs().get(0));
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#getAvailableReaderConfigurations
	 * ()
	 */
	@Override
	public Map<String, String> getAvailableReaderConfigurations()
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#getCommandConfiguration(java
	 * .lang.String)
	 */
	@Override
	public AttributeList getCommandConfiguration(String commandConfigurationID)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#getCommandConfigurationDescription
	 * (java.lang.String)
	 */
	@Override
	public MBeanInfo getCommandConfigurationDescription(
			String commandConfigurationFactoryID) throws RemoteException {
		AbstractCommandConfigurationFactory factory = commandConfigDAO
				.getCommandConfigurationFactoryFactoryFromConfigFactoryID(commandConfigurationFactoryID);

		if (factory != null) {
			Configuration emptyConfig = factory
					.getEmptyConfiguration(commandConfigurationFactoryID);
			return emptyConfig.getMBeanInfo();
		} else
			return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#getReaderConfigurationDescription
	 * (java.lang.String)
	 */
	@Override
	public MBeanInfo getReaderConfigurationDescription(
			String readerConfigurationFactoryID) throws RemoteException {
		AbstractReaderConfigurationFactory<?> readerConfigFactory = readerConfigDAO
				.getReaderConfigurationFactory(readerConfigurationFactoryID);
		return readerConfigFactory.getEmptyConfiguration(
				readerConfigurationFactoryID).getMBeanInfo();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#getReaderConfigurationProperties
	 * (java.lang.String)
	 */
	@Override
	public AttributeList getReaderConfigurationProperties(
			String readerConfigurationID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
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
	 * org.rifidi.edge.core.rmi.EdgeServerStub#setCommandConfigurationProperties
	 * (java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public AttributeList setCommandConfigurationProperties(
			String CommandConfigurationID,
			AttributeList commandConfigurationProperties) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#setReaderConfigurationProperties
	 * (java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public AttributeList setReaderConfigurationProperties(
			String ReaderConfigurationID,
			AttributeList readerConfigurationProperties) {
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
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#startReaderSession(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public String startReaderSession(String readerConfigurationName,
			String commandFactoryName) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.EdgeServerStub#stopReaderSession(java.lang
	 * .String)
	 */
	@Override
	public void stopReaderSession(String readerSessionName)
			throws RemoteException {
		// TODO Auto-generated method stub

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

}
