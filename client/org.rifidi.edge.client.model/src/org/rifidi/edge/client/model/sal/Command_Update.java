/*
 * Command_Update.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.model.sal;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.api.rmi.dto.CommandConfigFactoryDTO;
import org.rifidi.edge.api.rmi.dto.CommandConfigurationDTO;
import org.rifidi.edge.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCGetCommandConfigDescription;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCGetCommandConfigFactories;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCGetCommandConfigurations;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCServerDescription;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESMS_GetStartupTimestampCommand;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReaderDescription;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReaderFactories;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReaders;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.rmi.proxycache.exceptions.AuthenticationException;
import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;

/**
 * A command that gets executed when someone wants to make sure the
 * RemoteEdgeServer is really in synch with the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class Command_Update implements RemoteEdgeServerCommand {

	/** The remote edge server to update */
	private RemoteEdgeServer remoteEdgeServer;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(Command_Update.class);
	/** The set of ReaderFactories */
	private Set<ReaderFactoryDTO> readerFactoryDTOs;
	/** The set of readers */
	private Set<ReaderDTO> readerDTOs;
	/** The set of commandConfigPluginDTOs */
	private Set<CommandConfigFactoryDTO> commandConfigFactoryDTO;
	/** A mapping between Reader Factories and MBeans */
	private Map<String, MBeanInfo> factoryIDToMBeanInfo;
	/** <readerID, Map<commandType, MBeanInfo> */
	private Map<String, Map<String, MBeanInfo>> readerIdsToCommandMBeanMap;
	/** The set of command configuration DTOS */
	private Set<CommandConfigurationDTO> commandConfigurationDTOs;
	/** Variable that is set in case of an error */
	private boolean error = false;

	/***
	 * Constructor
	 * 
	 * @param server
	 *            The RemoteEdgeServer to refresh
	 */
	public Command_Update(RemoteEdgeServer server) {
		this.remoteEdgeServer = server;
		factoryIDToMBeanInfo = new HashMap<String, MBeanInfo>();
		readerIdsToCommandMBeanMap = new HashMap<String, Map<String, MBeanInfo>>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.model.sal.RemoteEdgeServerCommand#execute()
	 */
	@Override
	public void execute() {
		try {
			// first get the timestamp from the server
			ESMS_GetStartupTimestampCommand getTimestampCall = new ESMS_GetStartupTimestampCommand(
					remoteEdgeServer.getESServerDescription());
			Long timestamp = getTimestampCall.makeCall();

			// if the server has restarted since the last time we updated, we
			// need to disconnect and reconnect
			Long startupTime = remoteEdgeServer.startupTime;
			if ((!startupTime.equals(0l)) && (!timestamp.equals(startupTime))) {
				RequestExecuterSingleton.getInstance().scheduleRequest(
						new Command_Refresh(remoteEdgeServer));
				error = true;
				return;
			}
			remoteEdgeServer.startupTime = timestamp;

			RS_ServerDescription rs_description = remoteEdgeServer
					.getRSServerDescription();
			CCServerDescription cc_description = remoteEdgeServer
					.getCCServerDescription();
			RS_GetReaderFactories rsGetFactoriesCall = new RS_GetReaderFactories(
					rs_description);
			readerFactoryDTOs = rsGetFactoriesCall.makeCall();
			for (ReaderFactoryDTO factory : readerFactoryDTOs) {
				RS_GetReaderDescription descriptionCall = new RS_GetReaderDescription(
						rs_description, factory.getReaderFactoryID());
				factoryIDToMBeanInfo.put(factory.getReaderFactoryID(),
						descriptionCall.makeCall());
			}
			logger.debug(readerFactoryDTOs.size() + " reader Factories found");

			RS_GetReaders rsGetReaderCall = new RS_GetReaders(rs_description);
			readerDTOs = rsGetReaderCall.makeCall();

			logger.debug(readerDTOs.size() + " readers found");

			CCGetCommandConfigFactories getCommandTypes = new CCGetCommandConfigFactories(
					cc_description);
			this.commandConfigFactoryDTO = getCommandTypes.makeCall();
			for (CommandConfigFactoryDTO factory : commandConfigFactoryDTO) {

				// add a new HashMap to the readerIdsToCommandMBeanMap if there
				// is not already one there for the particular reader type this
				// commandConfigFactory belongs to
				if (!readerIdsToCommandMBeanMap.containsKey(factory
						.getReaderFactoryID())) {
					readerIdsToCommandMBeanMap.put(
							factory.getReaderFactoryID(),
							new HashMap<String, MBeanInfo>());
				}
				// get the commanFactoryMap for the particular reader type
				Map<String, MBeanInfo> commandFactoryToMbeanInfo = readerIdsToCommandMBeanMap
						.get(factory.getReaderFactoryID());

				CCGetCommandConfigDescription getDescription = new CCGetCommandConfigDescription(
						cc_description, factory.getCommandFactoryID());
				MBeanInfo info = getDescription.makeCall();
				if (info == null) {
					logger.warn("Info for " + factory.getCommandFactoryID()
							+ " is null!");
				} else {
					commandFactoryToMbeanInfo.put(
							factory.getCommandFactoryID(), info);
					this.readerIdsToCommandMBeanMap.put(factory
							.getReaderFactoryID(), commandFactoryToMbeanInfo);
				}
			}

			CCGetCommandConfigurations getCommandConfigurations = new CCGetCommandConfigurations(
					remoteEdgeServer.getCCServerDescription());
			this.commandConfigurationDTOs = getCommandConfigurations.makeCall();

		} catch (ServerUnavailable e) {
			logger.debug("Server Unavailable ", e);
			error = true;
			remoteEdgeServer.disconnect();
		} catch (AuthenticationException e) {
			error = true;
			remoteEdgeServer.disconnect();
			remoteEdgeServer.handleAuthenticationException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.RemoteEdgeServerCommand#executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		if (!error) {
			for (ReaderFactoryDTO factory : readerFactoryDTOs) {
				String readerFactoryID = factory.getReaderFactoryID();
				MBeanInfo info = factoryIDToMBeanInfo.get(readerFactoryID);
				remoteEdgeServer.readerFactories.put(readerFactoryID,
						new RemoteReaderFactory(factory, info));
			}
			for (ReaderDTO reader : readerDTOs) {
				RemoteReaderFactory factory = (RemoteReaderFactory) remoteEdgeServer.readerFactories
						.get(reader.getReaderFactoryID());
				if (factory != null) {
					remoteEdgeServer.remoteReaders.put(reader.getReaderID(),
							new RemoteReader(reader, factory));
				}
			}

			for (CommandConfigFactoryDTO commandFactory : this.commandConfigFactoryDTO) {
				Map<String, MBeanInfo> commandMBeanMap = readerIdsToCommandMBeanMap
						.get(commandFactory.getReaderFactoryID());
				MBeanInfo info = commandMBeanMap.get(commandFactory
						.getCommandFactoryID());
				remoteEdgeServer.commandConfigFactories.put(commandFactory
						.getCommandFactoryID(), new RemoteCommandConfigFactory(
						commandFactory, info));
			}

			// for every command configuration, create a new
			// RemoteCommandComnfiguration.
			for (CommandConfigurationDTO commandConfig : this.commandConfigurationDTOs) {
				// first we need to find the RemoteCommandConfigFactory which
				// can create instances of this command
				for (CommandConfigFactoryDTO factoryDTO : commandConfigFactoryDTO) {
					if (factoryDTO.getCommandFactoryID().equals(
							commandConfig.getCommandConfigFactoryID())) {

						RemoteCommandConfigFactory factory = (RemoteCommandConfigFactory) remoteEdgeServer.commandConfigFactories
								.get(factoryDTO.getCommandFactoryID());

						// yay! We've found the right factory
						if (factory != null) {
							remoteEdgeServer.commandConfigurations.put(
									commandConfig.getCommandConfigID(),
									new RemoteCommandConfiguration(
											commandConfig, factory));
						}
					}
				}

			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.model.sal.RemoteEdgeServerCommand#getType()
	 */
	@Override
	public String getType() {
		return "UPDATE";
	}

}
