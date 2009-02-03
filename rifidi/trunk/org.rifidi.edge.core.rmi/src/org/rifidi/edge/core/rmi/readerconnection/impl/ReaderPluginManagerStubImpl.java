/*
 *  ReaderPluginManagerStubImpl.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.exceptions.DynamicSWTFormAnnotationException;
import org.rifidi.dynamicswtforms.xml.processor.DynamicSWTFormXMLProcessor;
import org.rifidi.edge.common.utilities.dom.DomHelper;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.api.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.api.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.api.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderPluginManagerStub;
import org.rifidi.edge.core.rmi.api.readerconnection.exceptions.RifidiPluginDoesNotExistException;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.CommandGroupMap;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;
import org.w3c.dom.Document;

/**
 * This is the implementation for the ReaderPluginManager Stub
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderPluginManagerStubImpl extends UnicastRemoteObject implements
		ReaderPluginManagerStub, Unreferenced {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ReaderPluginService readerPluginService;

	private DynamicSWTFormXMLProcessor dynamicSWTFormXMLProcessor;

	private Log logger = LogFactory.getLog(ReaderPluginManagerStubImpl.class);

	/**
	 * 
	 */
	public ReaderPluginManagerStubImpl() throws RemoteException {
		super();
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub#
	 * getAvailableReaderPlugins()
	 */

	public List<String> getAvailableReaderPlugins() throws RemoteException {
		logger.debug("RMI Call: getAvailableReaderPlugins");
		ArrayList<String> retVal = new ArrayList<String>(readerPluginService
				.getAllReaderInfos());
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub#
	 * getReaderPluginXML(java.lang.String)
	 */
	@Override
	public String getReaderPluginXML(String readerInfoClassName)
			throws RifidiReaderPluginXMLNotFoundException {
		logger.debug("RMI Call: getReaderPluginXML");
		ReaderPlugin plugin = readerPluginService
				.getReaderPlugin(readerInfoClassName);
		return plugin.getReaderPluginXML();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub#
	 * getReaderInfoAnnotation(java.lang.String)
	 */
	@Override
	public String getReaderInfoAnnotation(String readerInfoClassName)
			throws RemoteException, RifidiReaderInfoNotFoundException {
		logger.debug("RMI Call: getReaderInfoAnnotation");
		try {
			Document doc = readerPluginService
					.getReaderInfoAnnotation(readerInfoClassName);
			TransformerFactory transfac = TransformerFactory.newInstance();
			Transformer trans = transfac.newTransformer();

			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");

			// Print the DOM node

			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(doc);
			trans.transform(source, result);
			return sw.toString();
		} catch (TransformerException ex) {
			logger
					.error("Cannot get reader info annotation due to transformer exception");
			return null;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub#
	 * getAvailableProperties()
	 */
	@Override
	public List<CommandGroupMap> getAvailableProperties(
			String readerInfoClassName)
			throws RifidiPluginDoesNotExistException {
		ReaderPlugin plugin = readerPluginService
				.getReaderPlugin(readerInfoClassName);
		if (plugin != null) {
			List<CommandGroupMap> commands = new ArrayList<CommandGroupMap>();
			for (String propertyName : plugin.getProperties()) {
				CommandGroupMap map = new CommandGroupMap();
				map.setName(propertyName);
				map.setGroups(new HashSet<String>(plugin.getProperty(
						propertyName).getGroups()));
				commands.add(map);
			}
			return commands;
		} else {
			throw new RifidiPluginDoesNotExistException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub#
	 * getAvaliableCommands(java.lang.String)
	 */
	@Override
	public List<CommandGroupMap> getAvaliableCommands(String readerInfoClassName)
			throws RifidiPluginDoesNotExistException {
		ReaderPlugin plugin = readerPluginService
				.getReaderPlugin(readerInfoClassName);
		if (plugin != null) {
			List<CommandGroupMap> commands = new ArrayList<CommandGroupMap>();
			for (String commandName : plugin.getCommands()) {
				CommandGroupMap map = new CommandGroupMap();
				map.setName(commandName);
				map.setGroups(new HashSet<String>(plugin
						.getCommand(commandName).getGroups()));
				commands.add(map);
			}
			return commands;
		} else {
			throw new RifidiPluginDoesNotExistException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub#
	 * getCommandAnnotations(java.lang.String)
	 */
	@Override
	public String getCommandAnnotations(String readerInfoClassName)
			throws RifidiPluginDoesNotExistException,
			DynamicSWTFormAnnotationException {
		logger.debug("RMI Call: getCommandAnnotations");
		ReaderPlugin plugin = readerPluginService
				.getReaderPlugin(readerInfoClassName);
		if (plugin != null) {
			List<Class<?>> classes = new ArrayList<Class<?>>();
			if (plugin.getCommands().size() == 0) {
				logger.debug("No available commands");
			}
			for (String command : plugin.getCommands()) {
				try {
					CommandDescription cd = plugin.getCommand(command);
					classes.add(Class.forName(cd.getClassname()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					logger.debug("ClassNotFoundException when "
							+ "trying to instantite class for "
							+ plugin.getCommand(command));
				}
			}
			try {
				return DomHelper.toString(this.dynamicSWTFormXMLProcessor
						.processAnnotation("CommandDescriptors", classes));
			} catch (TransformerException e) {
				throw new DynamicSWTFormAnnotationException(
						"Transformer Exception when converting DOM to string");
			}
		} else {
			throw new RifidiPluginDoesNotExistException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.ReaderPluginManagerStub#
	 * getPropertyAnnotations(java.lang.String)
	 */
	@Override
	public String getPropertyAnnotations(String readerInfoClassName)
			throws DynamicSWTFormAnnotationException,
			RifidiPluginDoesNotExistException {
		logger.debug("RMI Call: getPropertyAnnotations");
		ReaderPlugin plugin = readerPluginService
				.getReaderPlugin(readerInfoClassName);
		if (plugin != null) {
			List<Class<?>> classes = new ArrayList<Class<?>>();
			for (String property : plugin.getProperties()) {
				try {
					CommandDescription cd = plugin.getProperty(property);
					classes.add(Class.forName(cd.getClassname()));
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					logger.debug("ClassNotFoundException when "
							+ "trying to instantite class for "
							+ plugin.getProperty(property).getClassname());
				}
			}
			try {
				String s = DomHelper
						.toString(this.dynamicSWTFormXMLProcessor
								.processAnnotation("ReaderPropertyDescriptors",
										classes));
				return s;
			} catch (TransformerException e) {
				logger.error("Transformer Exception");
				throw new DynamicSWTFormAnnotationException(
						"Transformer Exception when converting DOM to string");
			}
		} else {
			throw new RifidiPluginDoesNotExistException();
		}
	}

	/**
	 * Inject method to obtain a instance of the ReaderPluginService from the
	 * RegistryService Framework
	 * 
	 * @param readerPluginService
	 *            ReaderPluginService instance
	 */
	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

	@Inject
	public void setWidgetAnnoationProcessorService(
			DynamicSWTFormXMLProcessor service) {
		dynamicSWTFormXMLProcessor = service;
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
