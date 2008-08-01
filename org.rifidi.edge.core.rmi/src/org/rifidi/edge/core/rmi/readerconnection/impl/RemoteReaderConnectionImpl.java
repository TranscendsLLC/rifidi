package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.io.IOException;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Collection;

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

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	 * getReaderInfo()
	 */
	@Override
	public ReaderInfo getReaderInfo() throws RemoteException {
		return readerSession.getReaderInfo();
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

	//
	// /*
	// * (non-Javadoc)
	// *
	// * @see org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection#
	// startTagStream(java.lang.String)
	// */
	// @Override
	// public long startTagStream(String configuration) throws RemoteException,
	// RifidiConnectionException, RifidiCommandInterruptedException,
	// RifidiCommandNotFoundException, RifidiInvalidConfigurationException {
	// return executeCommand("<TagStreaming></TagStreaming>");
	// }

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

}
