package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.Collection;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.dom.DomHelper;
import org.rifidi.edge.core.exceptions.RifidiCannotRestartCommandException;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiCommandNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidConfigurationException;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

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
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * commandStatus(long)
	 */
	@Override
	public String commandStatus(long id) {
		return readerSession.commandStatus(id).toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * commandStatus()
	 */
	@Override
	public String commandStatus() {
		return readerSession.commandStatus().toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * curExecutingCommand()
	 */
	@Override
	public String curExecutingCommand() throws RemoteException {
		return readerSession.curExecutingCommand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * curExecutingCommandID()
	 */
	@Override
	public long curExecutingCommandID() throws RemoteException {
		return readerSession.curExecutingCommandID();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * executeCommand(java.lang.String, java.lang.String)
	 */
	@Override
	// TODO: remove stack traces
	public long executeCommand(String configuration) throws RemoteException,
			RifidiConnectionException, RifidiCommandInterruptedException,
			RifidiCommandNotFoundException, RifidiInvalidConfigurationException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader reader = new StringReader(configuration);
			InputSource inputSource = new InputSource(reader);
			Document doc = builder.parse(inputSource);
			return readerSession.executeCommand(doc);
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException", e);
			throw new RemoteException("ParserConfigurationException", e);
		} catch (SAXException e) {
			logger.error("SAXException", e);
			throw new RemoteException("SAXException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
			throw new RemoteException("IOException", e);
		}
	}

	@Override
	public String executeProperty(String configuration) throws RemoteException,
			RifidiConnectionException, RifidiCommandNotFoundException,
			RifidiCommandInterruptedException,
			RifidiInvalidConfigurationException,
			RifidiCannotRestartCommandException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			StringReader reader = new StringReader(configuration);
			InputSource inputSource = new InputSource(reader);
			Document doc = builder.parse(inputSource);
			Document d = readerSession.executeProperty(doc);

			String s = DomHelper.toString(d);
			return s;
		} catch (SAXException e) {
			logger.error("SAXException", e);
			throw new RemoteException("SAXException", e);
		} catch (IOException e) {
			logger.error("IOException", e);
			throw new RemoteException("IOException", e);
		} catch (TransformerException e) {
			logger.error("TransformerException", e);
			throw new RemoteException("TransformerException", e);
		} catch (ParserConfigurationException e) {
			logger.error("ParserConfigurationException", e);
			throw new RemoteException("ParserConfigurationException", e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * getMessageQueueName()
	 */
	@Override
	public String getMessageQueueName() throws RemoteException {
		return readerSession.getMessageQueueName();
	}

	@Override
	public String getReaderInfo() throws RemoteException,
			RifidiReaderInfoNotFoundException {
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
			readerSession.setReaderInfo(newReaderInfo);
		} catch (JAXBException e) {
			throw new RifidiReaderInfoNotFoundException(e.getMessage());
		}
	}

	@Override
	public String getReaderInfoClassName() throws RemoteException {
		return readerSession.getReaderInfo().getClass().getName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * getReaderState()
	 */
	@Override
	public String getReaderState() throws RemoteException {
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
		readerSession.resetReaderSession();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * stopCurCommand(boolean)
	 */
	@Override
	public boolean stopCurCommand(boolean force) throws RemoteException {
		return readerSession.stopCurCommand(force);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * stopCurCommand(boolean, long)
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

	// === Get Commands and Properties as a list of Strings

	@Override
	public Collection<String> getAvailableCommandGroups()
			throws RemoteException {
		return readerSession.getAvailableCommandGroups();
	}

	@Override
	public Collection<String> getAvailableCommands() throws RemoteException {
		return readerSession.getAvailableCommands();
	}

	@Override
	public Collection<String> getAvailableCommands(String groupName)
			throws RemoteException {
		return readerSession.getCommandsForGroup(groupName);
	}

	@Override
	public Collection<String> getAvailablePropertyGroups()
			throws RemoteException {
		return readerSession.getAvailablePropertyGroups();
	}

	@Override
	public Collection<String> getAvailableProperties() throws RemoteException {
		return readerSession.getAvailableProperties();
	}

	@Override
	public Collection<String> getAvailableProperties(String groupName)
			throws RemoteException {
		return readerSession.getPropertiesForGroup(groupName);
	}

	@Override
	public void disable() throws RemoteException {
		readerSession.disable();

	}

	@Override
	public void enable() throws RemoteException {
		readerSession.enable();

	}

}
