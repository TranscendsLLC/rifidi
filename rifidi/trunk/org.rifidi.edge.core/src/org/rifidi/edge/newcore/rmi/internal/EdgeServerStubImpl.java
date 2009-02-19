/**
 * 
 */
package org.rifidi.edge.newcore.rmi.internal;

import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.newcore.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.newcore.internal.CommandConfigurationDAO;
import org.rifidi.edge.newcore.internal.ReaderConfigurationDAO;
import org.rifidi.edge.newcore.readers.AbstractReaderConfigurationFactory;
import org.rifidi.edge.newcore.readersession.ReaderSessionDAO;
import org.rifidi.edge.newcore.rmi.EdgeServerStub;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#createCommandFactory(javax
	 * .management.AttributeList)
	 */
	@Override
	public String createCommandFactory(AttributeList commandConfiguration)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#createReaderConfiguration(
	 * javax.management.AttributeList)
	 */
	@Override
	public String createReaderConfiguration(AttributeList readerConfiguration)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#deleteCommandFactory(java.
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
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#deleteReaderConfiguration(
	 * java.lang.String)
	 */
	@Override
	public void deleteReaderConfiguration(String readerConfigurationName)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#getAvailableCommandFactories
	 * (java. lang.String)
	 */
	@Override
	public Set<String> getAvailableCommandFactories(String readerPluginName)
			throws RemoteException {
		// return set
		Set<String> retVal = new HashSet<String>();

		// Find readerPlugin that matches readerPluginName
		AbstractReaderConfigurationFactory<?> readerConfigFactory = readerConfigDAO
				.getReaderConfigurationFactory(readerPluginName);

		// if we did not find any readerConfigFactories that matched, return
		if (readerConfigFactory == null) {
			logger.debug("No Reader Configuration Factory found with id: "
					+ readerPluginName);
			return retVal;
		}

		// Find CommandConfigFactory that has the ID from the
		// readerConfiguFactory
		Iterator<AbstractCommandConfigurationFactory> commandIter = commandConfigDAO
				.getCurrentCommandConfigurationFactories().iterator();

		AbstractCommandConfigurationFactory currentCommandFactory = null;
		while (commandIter.hasNext()) {
			currentCommandFactory = commandIter.next();
			if (currentCommandFactory.getID().equals(
					readerConfigFactory.getCommandConfigurationFactoryName())) {
				break;
			}

		}

		// if we did not find any commandConfigFactories that matched, return
		if (currentCommandFactory == null) {
			logger.debug("No Command Configuration Factory found with id: "
					+ readerConfigFactory.getCommandConfigurationFactoryName());
			return retVal;
		}
		retVal.addAll(currentCommandFactory.getFactoryIDs());
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#getAvailableReaderPlugins()
	 */
	@Override
	public Set<String> getAvailableReaderPlugins() throws RemoteException {
		Iterator<AbstractReaderConfigurationFactory<?>> iter = readerConfigDAO
				.getCurrentReaderConfigurationFactories().iterator();
		Set<String> retVal = new HashSet<String>();
		AbstractReaderConfigurationFactory<?> current = null;
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
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#getCommandConfigurationDescription
	 * (java.lang.String)
	 */
	@Override
	public MBeanInfo getCommandConfigurationDescription(
			String commandFactoryName) throws RemoteException {
		Configuration commandConfig = commandConfigDAO
				.getDefaultCommandConfiguration(commandFactoryName);
		if (commandConfig != null) {
			return commandConfig.getMBeanInfo();
		} else
			return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#getReaderConfigurationDescription
	 * (java.lang.String)
	 */
	@Override
	public MBeanInfo getReaderConfigurationDescription(String readerPluginName)
			throws RemoteException {
		AbstractReaderConfigurationFactory<?> readerConfigFactory = readerConfigDAO
				.getReaderConfigurationFactory(readerPluginName);
		return readerConfigFactory.getEmptyConfiguration(readerPluginName)
				.getMBeanInfo();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#startReaderSession(java.lang
	 * .String, java.lang.String)
	 */
	@Override
	public String startReaderSession(String readerConfigurationName,
			String commandFactoryName) throws RemoteException {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.rmi.EdgeServerStub#stopReaderSession(java.lang
	 * .String)
	 */
	@Override
	public void stopReaderSession(String readerSessionName)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

}
