/**
 * 
 */
package org.rifidi.edge.core.rmi.internal;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.Command;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.daos.ConfigurationDAO;
import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.AbstractReaderFactory;
import org.rifidi.edge.core.readers.ReaderSession;

/**
 * @author kyle
 * 
 */
public class ReaderConfigurationStubImpl implements ReaderStub {

	/** A data access object for the readerSession configuration services */
	private ReaderDAO readerConfigDAO;
	/** A data access object for configurations in OSGi */
	private ConfigurationDAO configurationDAO;
	/** A data access object for commands and command factories */
	private CommandDAO commandDAO;
	/** A logger for this class */
	private static final Log logger = LogFactory
			.getLog(ReaderConfigurationStubImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.ReaderStub#createReaderConfiguration
	 * (java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public String createReader(String readerConfigurationFactoryID,
			AttributeList readerConfigurationProperties) throws RemoteException {

		// get the readerSession configuration factory that corresponds to the
		// ID
		AbstractReaderFactory<?> readerConfigFactory = this.readerConfigDAO
				.getReaderFactoryByID(readerConfigurationFactoryID);

		// Get an empty configuration
		Configuration readerConfiguration = readerConfigFactory
				.getEmptyConfiguration(readerConfigurationFactoryID);

		// set the attributes on the configuration
		readerConfiguration.setAttributes(readerConfigurationProperties);

		// create the service
		readerConfigFactory.createService(readerConfiguration);

		// return the ID
		return readerConfiguration.getServiceID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.rmi.ReaderStub#deleteReaderConfiguration
	 * (java.lang.String)
	 */
	@Override
	public void deleteReader(String readerConfigurationID)
			throws RemoteException {
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
		AbstractReader<?> readerConfig = readerConfigDAO
				.getReaderByID(readerConfigurationID);
		if (config != null) {
			config.destroy();
		} else {
			logger.warn("No configuraion found with ID: "
					+ readerConfigurationID);
		}

		if (readerConfig != null) {
			readerConfig.destroy();
		} else {
			logger.warn("No readerSession configuraion found with ID: "
					+ readerConfigurationID);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getAvailableReaderConfigurationFactories()
	 */
	@Override
	public Set<String> getReaderFactories() throws RemoteException {
		Set<String> retVal = new HashSet<String>();
		Set<AbstractReaderFactory<?>> configFactories = this.readerConfigDAO
				.getReaderFactories();
		Iterator<AbstractReaderFactory<?>> iter = configFactories.iterator();
		while (iter.hasNext()) {
			AbstractReaderFactory<?> factory = iter.next();
			retVal.add(factory.getFactoryIDs().get(0));
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getAvailableReaderConfigurations()
	 */
	@Override
	public Set<ReaderDTO> getReaders() throws RemoteException {
		Set<ReaderDTO> retVal = new HashSet<ReaderDTO>();

		// Get all ReaderConfigurations
		Set<AbstractReader<?>> configurations = readerConfigDAO.getReaders();
		for (AbstractReader<?> readerConfig : configurations) {
			// get the ID of the readerSession configuration
			String readerID = readerConfig.getID();
			// look up the associated service configuration object for the
			// readerSession configuration
			Configuration config = configurationDAO.getConfiguration(readerID);
			if (config == null) {
				logger.debug("No Configuration Object with ID " + readerID
						+ " is available");
				break;
			}
			String factoryID = config.getFactoryID();
			AttributeList attrs = config.getAttributes(config
					.getAttributeNames());
			List<SessionDTO> sessionDTOs = buildSessionDTO(readerConfig
					.getReaderSessions());
			retVal.add(new ReaderDTO(readerID, factoryID, attrs, sessionDTOs));

		}

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getReaderConfigurationDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getReaderDescription(String readerConfigurationFactoryID)
			throws RemoteException {
		AbstractReaderFactory<?> configFactory = readerConfigDAO
				.getReaderFactoryByID(readerConfigurationFactoryID);
		if (configFactory != null) {
			Configuration config = configFactory
					.getEmptyConfiguration(readerConfigurationFactoryID);
			return config.getMBeanInfo();
		} else {
			logger.warn("No ReaderSession Configuration Factory with ID "
					+ readerConfigurationFactoryID + " is available");
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getReaderConfigurationProperties(java.lang.String)
	 */
	@Override
	public AttributeList getReaderProperties(String readerConfigurationID)
			throws RemoteException {
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
		if (config != null) {
			// find out the names of all the attributes
			List<String> attributeNames = new ArrayList<String>();
			for (MBeanAttributeInfo attrInfo : config.getMBeanInfo()
					.getAttributes()) {
				attributeNames.add(attrInfo.getName());
			}

			// convert name arraylist to string array
			String[] names = new String[attributeNames.size()];
			attributeNames.toArray(names);

			// get the attributes
			return config.getAttributes(names);
		} else {
			logger.warn("No Configuration object with ID "
					+ readerConfigurationID + " is available");
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.ReaderStub#createSession(java.lang.String)
	 */
	@Override
	public List<SessionDTO> createSession(String readerID)
			throws RemoteException {
		AbstractReader<?> reader = this.readerConfigDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return null;
		}
		reader.createReaderSession();
		logger.info("New reader session created on Reader " + readerID);
		return buildSessionDTO(reader.getReaderSessions());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.ReaderStub#killCommand(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void killCommand(String readerID, Integer sessionIndex,
			Integer processID) {
		assert (sessionIndex >= 0);
		AbstractReader<?> reader = readerConfigDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return;
		}
		if (sessionIndex < reader.getReaderSessions().size()) {
			reader.getReaderSessions().get(sessionIndex).killComand(processID);
			logger.info("Command with processID " + processID
					+ " killed on Session " + sessionIndex + " on reader "
					+ readerID);
		} else {
			logger.warn("No session with index " + sessionIndex
					+ " is available");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.ReaderStub#startSession(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void startSession(String readerID, Integer sessionIndex)
			throws RemoteException {
		assert (sessionIndex >= 0);
		final String finalReaderID = readerID;
		final Integer finalSessionIndex = sessionIndex;

		Thread connectThread = new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				AbstractReader<?> reader = readerConfigDAO
						.getReaderByID(finalReaderID);
				if (reader == null) {
					logger.warn("No reader with ID " + finalReaderID
							+ " is available");
					return;
				}
				try {
					if (finalSessionIndex < reader.getReaderSessions().size()) {
						reader.getReaderSessions().get(finalSessionIndex)
								.connect();
						logger.info("Session " + finalReaderID + " on Reader "
								+ finalReaderID + " has started");
						reader.applyPropertyChanges();
					} else {
						logger.warn("No session with index "
								+ finalSessionIndex + " is available");
						return;
					}
				} catch (IOException e) {
					logger
							.error("Reader Session" + finalSessionIndex
									+ " on Reader " + finalReaderID
									+ " cannot connect");
				}
			}

		};

		connectThread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.ReaderStub#stopSession(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void stopSession(String readerID, Integer sessionIndex)
			throws RemoteException {
		assert (sessionIndex >= 0);
		AbstractReader<?> reader = readerConfigDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return;
		}

		if (sessionIndex < reader.getReaderSessions().size()) {
			reader.getReaderSessions().get(sessionIndex).disconnect();
		} else {
			logger.warn("No session with index " + sessionIndex
					+ " is available");
			return;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.ReaderStub#submitCommand(java.lang.String,
	 * java.lang.String, java.lang.String, java.lang.Long,
	 * java.util.concurrent.TimeUnit)
	 */
	@Override
	public void submitCommand(String readerID, Integer sessionIndex,
			String commandID, Long repeatInterval, TimeUnit timeUnit) {
		assert (sessionIndex >= 0);
		AbstractReader<?> reader = readerConfigDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return;
		}
		AbstractCommandConfiguration<?> commandConfig = commandDAO
				.getCommandByID(commandID);
		if (commandConfig == null) {
			logger.warn("No command with ID " + commandID + " is available");
			return;
		}

		if (sessionIndex < reader.getReaderSessions().size()) {
			Command command = commandConfig.getCommand();
			reader.getReaderSessions().get(sessionIndex).submit(command,
					repeatInterval, timeUnit);
			logger.info("Command with ID " + commandID
					+ " submitted to session " + sessionIndex + " of reader "
					+ readerID + " with repeat interval " + repeatInterval
					+ " " + timeUnit);
		} else {
			logger.warn("No session with index " + sessionIndex
					+ " is available");
			return;
		}

	}

	private List<SessionDTO> buildSessionDTO(List<ReaderSession> sessions) {
		ArrayList<SessionDTO> sessionDTOs = new ArrayList<SessionDTO>();
		for (ReaderSession session : sessions) {
			// get all commands from session
			HashMap<Integer, String> commandMap = new HashMap<Integer, String>();
			for (Integer processID : session.currentCommands().keySet()) {
				commandMap.put(processID, session.currentCommands().get(
						processID).getCommandID());
			}
			sessionDTOs.add(new SessionDTO(session.getStatus(), commandMap));
		}
		return sessionDTOs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * setReaderConfigurationProperties(java.lang.String,
	 * javax.management.AttributeList)
	 */
	@Override
	public void setReaderProperties(String readerConfigurationID,
			AttributeList readerConfigurationProperties) throws RemoteException {
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
		AbstractReader<?> reader = readerConfigDAO.getReaderByID(readerConfigurationID);
		if ((config != null) && (reader!=null)) {
			config.setAttributes(readerConfigurationProperties);
			reader.applyPropertyChanges();
		} else {
			logger.warn("No Configuration object with ID "
					+ readerConfigurationID + " is available");
		}
	}

	/**
	 * Used by spring to set the ReaderConfigDAO
	 * 
	 * @param readerConfigDAO
	 *            the readerConfigDAO to set
	 */
	public void setReaderConfigDAO(ReaderDAO readerConfigDAO) {
		this.readerConfigDAO = readerConfigDAO;
	}

	/**
	 * @param configurationDAO
	 *            the configurationDAO to set
	 */
	public void setConfigurationDAO(ConfigurationDAO configurationDAO) {
		this.configurationDAO = configurationDAO;
	}

	/**
	 * @param commandDAO
	 *            the commandDAO to set
	 */
	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}
}
