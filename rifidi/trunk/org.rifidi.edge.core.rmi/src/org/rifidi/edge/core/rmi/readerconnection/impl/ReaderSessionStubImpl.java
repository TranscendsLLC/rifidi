package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.api.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.api.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;
import org.rifidi.edge.core.api.readerplugin.commands.CommandArgument;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.property.PropertyConfiguration;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderSessionStub;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.CommandInfo;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.ReaderSessionProperties;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * Implementation for a ReaderSessionStub
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderSessionStubImpl extends UnicastRemoteObject implements
		ReaderSessionStub, Unreferenced {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Log logger = LogFactory.getLog(ReaderSessionStubImpl.class);

	/**
	 * Internal ReaderSession which gets exposed with this
	 * RemoteReaderConnection
	 */
	private ReaderSession readerSession;

	private ReaderSessionService readerSessionService;

	/**
	 * Create a new RemoteReaderConnection associated with the ReaderSession
	 * 
	 * @param readerSession
	 *            with which the RemoteReaderConnection is associated
	 */
	public ReaderSessionStubImpl(ReaderSession readerSession)
			throws RemoteException {
		super();
		this.readerSession = readerSession;
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public void enable() throws RemoteException {
		logger.debug("RMI Call: enable");
		readerSession.enable();

	}

	@Override
	public void disable() throws RemoteException {
		logger.debug("RMI Call: disable");
		readerSession.disable();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * commandStatus(long)
	 */
	@Override
	public CommandInfo commandStatus(long id) {
		logger.debug("RMI Call: commandStatus");
		String name = readerSession.commandName(id);
		String status = readerSession.commandStatus(id).name();
		CommandInfo info = new CommandInfo(name, id, status);
		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * commandStatus()
	 */
	@Override
	public CommandInfo commandStatus() {
		logger.debug("RMI Call: commandStatus");
		String name = readerSession.curExecutingCommand();
		long id = readerSession.curExecutingCommandID();
		String status = readerSession.commandStatus().name();
		CommandInfo info = new CommandInfo(name, id, status);
		return info;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * executeCommand(java.lang.String, java.lang.String)
	 */
	@Override
	public long executeCommand(CommandConfiguration configuration)
			throws RemoteException, RifidiConnectionException,
			RifidiCommandInterruptedException, RifidiCommandNotFoundException {
		logger.debug("RMI Call: executeCommand");
		return readerSession.executeCommand(configuration);

	}

	@Override
	public PropertyConfiguration getProperty(Set<String> propertyNames)
			throws RemoteException, RifidiConnectionException,
			RifidiCommandNotFoundException, RifidiCommandInterruptedException,
			RifidiCannotRestartCommandException {
		logger.debug("RMI Call: getProperty");
		Set<CommandConfiguration> properties = new HashSet<CommandConfiguration>();
		for (String s : propertyNames) {
			properties.add(new CommandConfiguration(s,
					new HashSet<CommandArgument>()));
		}
		PropertyConfiguration config = new PropertyConfiguration(properties);
		try {
			return readerSession.executeProperty(config, false);
		} catch (RifidiInvalidConfigurationException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public PropertyConfiguration setProperty(PropertyConfiguration configuration)
			throws RemoteException, RifidiConnectionException,
			RifidiCommandNotFoundException, RifidiCommandInterruptedException,
			RifidiCannotRestartCommandException {
		logger.debug("RMI Call: setProperty");

		try {
			return readerSession.executeProperty(configuration,
					true);
		} catch (RifidiInvalidConfigurationException e) {
			logger.error(e);
			return null;
		}
	}

	@Override
	public String getReaderInfo() throws RemoteException,
			RifidiReaderInfoNotFoundException {
		logger.debug("RMI Call: getReaderInfo");
		try {
			JAXBContext context = JAXBContext.newInstance(readerSession
					.getReaderInfo().getClass());
			Marshaller m = context.createMarshaller();
			Writer writer = new StringWriter();
			m.marshal(readerSession.getReaderInfo(), writer);
			return writer.toString();

		} catch (JAXBException e) {
			throw new RifidiReaderInfoNotFoundException(e.getMessage());
		}

	}

	@Override
	public void setReaderInfo(String readerInfo) throws RemoteException,
			RifidiReaderInfoNotFoundException {
		logger.debug("RMI Call: setReaderInfo");
		try {

			JAXBContext context = JAXBContext.newInstance(readerSession
					.getReaderInfo().getClass());

			Unmarshaller unmarshaller = context.createUnmarshaller();
			Reader reader = new StringReader(readerInfo);
			ReaderInfo newReaderInfo = (ReaderInfo) unmarshaller
					.unmarshal(reader);
			if (newReaderInfo.getClass() != readerSession.getReaderInfo()
					.getClass()) {
				throw new RifidiReaderInfoNotFoundException(
						"Cannot set reader info of "
								+ readerSession.getReaderInfo().getClass()
										.getSimpleName()
								+ " to a reader info with type "
								+ newReaderInfo.getClass().getSimpleName());
			}
			readerSessionService.modifyReaderInfo(readerSession, newReaderInfo);
		} catch (JAXBException e) {
			throw new RifidiReaderInfoNotFoundException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * getReaderState()
	 */
	@Override
	public String getReaderState() throws RemoteException {
		logger.debug("RMI Call: getReaderState");
		return readerSession.getStatus().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * resetReaderConnection()
	 */
	@Override
	public void resetReaderConnection() throws RemoteException {
		logger.debug("RMI Call: resetReaderConnection");
		readerSession.resetReaderSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * stopCurCommand(boolean)
	 */
	@Override
	public void stopCurCommand(boolean force) throws RemoteException {
		logger.debug("RMI Call: stopCurCommand");
		readerSession.stopCurCommand(force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * stopCurCommand(boolean, long)
	 */
	@Override
	public void stopCurCommand(boolean force, long commandID)
			throws RemoteException {
		logger.debug("RMI Call: stopCurCommand");
		readerSession.stopCurCommand(force, commandID);
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

	/**
	 * Inject method to obtain a instance of the ReaderSessionService from the
	 * RegistryService Framework
	 * 
	 * @param readerSessionService
	 *            ReaderSessionService
	 */
	@Inject
	public void setReaderSessionService(
			ReaderSessionService readerSessionService) {
		this.readerSessionService = readerSessionService;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.ReaderSessionStub#
	 * getSessionProperties()
	 */
	@Override
	public ReaderSessionProperties getSessionProperties() {
		ReaderSessionProperties props = new ReaderSessionProperties();
		props.setMessageQueueName(readerSession.getMessageQueueName());
		props.setReaderInfoClassName(readerSession.getReaderInfo().getClass()
				.getName());
		return props;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.rmi.server.Unreferenced#unreferenced()
	 */
	@Override
	public void unreferenced() {
		logger.debug("Unreferenced");

	}

}
