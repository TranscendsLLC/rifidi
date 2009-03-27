/**
 * 
 */
package org.rifidi.edge.core.rmi.server;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.api.rmi.ReaderStub;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.api.rmi.dto.ReaderFactoryDTO;
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
	private ReaderDAO readerDAO;
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
		logger.debug("RMI call: createReader");

		// get the readerSession configuration factory that corresponds to the
		// ID
		AbstractReaderFactory<?> readerConfigFactory = this.readerDAO
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
		logger.debug("RMI call: deleteReader");
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
		AbstractReader<?> readerConfig = readerDAO
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
	public Set<ReaderFactoryDTO> getReaderFactories() throws RemoteException {
		logger.debug("RMI call: getReaderFactories");
		Set<ReaderFactoryDTO> retVal = new HashSet<ReaderFactoryDTO>();
		for (AbstractReaderFactory<?> factory : this.readerDAO
				.getReaderFactories()) {
			retVal.add(factory.getReaderFactoryDTO());
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.ReaderStub#getReaderFactory(java.lang.String
	 * )
	 */
	@Override
	public ReaderFactoryDTO getReaderFactory(String readerFactoryID)
			throws RemoteException {
		logger.debug("RMI call: getReaderFactory");
		AbstractReaderFactory<?> factory = this.readerDAO
				.getReaderFactoryByID(readerFactoryID);
		if (factory != null) {
			return factory.getReaderFactoryDTO();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.ReaderConfigurationStub#
	 * getAvailableReaderConfigurations()
	 */
	@Override
	public Set<ReaderDTO> getReaders() throws RemoteException {
		logger.debug("RMI call: getReaders");
		Set<ReaderDTO> retVal = new HashSet<ReaderDTO>();

		// Get all ReaderConfigurations
		Set<AbstractReader<?>> configurations = readerDAO.getReaders();
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
			retVal.add(readerConfig.getDTO(config));
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
		logger.debug("RMI call: getReaderDescription");
		AbstractReaderFactory<?> configFactory = readerDAO
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
	public ReaderDTO getReader(String readerConfigurationID)
			throws RemoteException {
		logger.debug("RMI call: getReader");
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
		AbstractReader<?> reader = readerDAO
				.getReaderByID(readerConfigurationID);
		if ((config != null) && (reader != null)) {
			return reader.getDTO(config);
		} else {
			logger.warn("No Configuration object with ID "
					+ readerConfigurationID + " is available");
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.api.rmi.ReaderStub#getSession(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public SessionDTO getSession(String readerID, String sessionID)
			throws RemoteException {
		AbstractReader<?> reader = this.readerDAO.getReaderByID(readerID);
		if (reader != null) {
			ReaderSession session = reader.getReaderSessions().get(sessionID);
			if (session != null) {
				return session.getDTO();
			}
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
	public Set<SessionDTO> createSession(String readerID)
			throws RemoteException {
		logger.debug("RMI call: createSession");
		AbstractReader<?> reader = this.readerDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return null;
		}
		reader.createReaderSession();
		logger.info("New reader session created on Reader " + readerID);
		Set<SessionDTO> sessionDTOs = new HashSet<SessionDTO>();
		for (ReaderSession s : reader.getReaderSessions().values()) {
			sessionDTOs.add(s.getDTO());
		}
		return sessionDTOs;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.api.rmi.ReaderStub#killCommand(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void killCommand(String readerID, String sessionID, Integer processID)
			throws RemoteException {
		logger.debug("RMI call: killCommand");
		AbstractReader<?> reader = readerDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return;
		}
		ReaderSession session = reader.getReaderSessions().get(sessionID);
		if (session != null) {
			reader.getReaderSessions().get(sessionID).killComand(processID);
			logger.info("Command with processID " + processID
					+ " killed on Session " + sessionID + " on reader "
					+ readerID);
		} else {
			logger.warn("No session with ID " + sessionID + " is available");
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
	public void startSession(String readerID, String sessionID)
			throws RemoteException {
		logger.debug("RMI call: startSession");
		final String finalReaderID = readerID;
		final String finalSessionIndex = sessionID;

		Thread connectThread = new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				AbstractReader<?> reader = readerDAO
						.getReaderByID(finalReaderID);
				if (reader == null) {
					logger.warn("No reader with ID " + finalReaderID
							+ " is available");
					return;
				}
				try {
					ReaderSession session = reader.getReaderSessions().get(
							finalSessionIndex);
					if (session != null) {
						session.connect();
						logger.info("Session " + finalReaderID + " on Reader "
								+ finalReaderID + " has started");
						reader.applyPropertyChanges();
					} else {
						logger.warn("No session with ID " + finalSessionIndex
								+ " is available");
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
	public void stopSession(String readerID, String sessionID)
			throws RemoteException {
		logger.debug("RMI call: stopSession");
		AbstractReader<?> reader = readerDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return;
		}

		ReaderSession session = reader.getReaderSessions().get(sessionID);
		if (session != null) {
			session.disconnect();
		} else {
			logger.warn("No session with index " + sessionID + " is available");
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
	public Integer submitCommand(String readerID, String sessionID,
			String commandID, Long repeatInterval, TimeUnit timeUnit)
			throws RemoteException {
		logger.debug("RMI call: submitCommand");
		AbstractReader<?> reader = readerDAO.getReaderByID(readerID);
		if (reader == null) {
			logger.warn("No reader with ID " + readerID + " is available");
			return null;
		}
		AbstractCommandConfiguration<?> commandConfig = commandDAO
				.getCommandByID(commandID);
		if (commandConfig == null) {
			logger.warn("No command with ID " + commandID + " is available");
			return null;
		}

		ReaderSession session = reader.getReaderSessions().get(sessionID);
		if (session != null) {
			Command command = commandConfig.getCommand();
			Integer processID = session.submit(command, repeatInterval,
					timeUnit);
			logger.info("Command with ID " + commandID
					+ " submitted to session " + sessionID + " of reader "
					+ readerID + " with repeat interval " + repeatInterval
					+ " " + timeUnit);
			return processID;
		} else {
			logger.warn("No session with ID " + sessionID + " is available");
			return null;
		}

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
		logger.debug("RMI call: setReaderProperties");
		Configuration config = configurationDAO
				.getConfiguration(readerConfigurationID);
		AbstractReader<?> reader = readerDAO
				.getReaderByID(readerConfigurationID);
		if ((config != null) && (reader != null)) {
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
	 * @param readerDAO
	 *            the readerDAO to set
	 */
	public void setReaderDAO(ReaderDAO readerConfigDAO) {
		this.readerDAO = readerConfigDAO;
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
