/*
 *  ReaderPluginManagerStub.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.core.rmi.readerconnection;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import org.rifidi.dynamicswtforms.xml.exceptions.DynamicSWTFormAnnotationException;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.rmi.readerconnection.exceptions.RifidiPluginDoesNotExistException;
import org.rifidi.edge.core.rmi.readerconnection.returnobjects.CommandGroupMap;

/**
 * The ReaderPluginManager is a remote object that has access to the reader
 * plugins (as opposed to sessions). Plugins contain information that is the
 * same for all sessions of the same type
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface ReaderPluginManagerStub extends Remote {

	/**
	 * Get a list of all Plugins. Each string is the qualified class name of the
	 * plugin's ReaderInfo class
	 * 
	 * @return A list of ReaderInfo Class names
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 */
	public List<String> getAvailableReaderPlugins() throws RemoteException;

	/**
	 * Get the ReaderPluginXML for the supplied reader plugin type. The Reader
	 * Plugin XML contins some information such as the name and description of
	 * the plugin, as well as a list of available commands and properties
	 * 
	 * @param readerInfoClassName
	 *            The qualified class name for the reader info of the desired
	 *            plugin
	 * @return The Reader Plugin XML.
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiReaderPluginXMLNotFoundException
	 *             If a plugin cannot be found for the supplied readerInfoClass
	 *             name
	 */
	public String getReaderPluginXML(String readerInfoClassName)
			throws RemoteException, RifidiReaderPluginXMLNotFoundException;

	/**
	 * This method returns meta information (such as fields and data types)
	 * about the ReaderInfo of the requested plugin. The meta infomation is
	 * returned in the dynamicSWTForm XML format. The meta information can be
	 * used to build a reader info for creating a reader session
	 * 
	 * @See org.rifidi.dynamicswtforms.xml.annotaions.Form
	 * @param readerInfoClassName
	 *            The qualified class name for the reader info of the desired
	 *            plugin
	 * @return XML that is the meta information of the reader info
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiReaderInfoNotFoundException
	 *             If a plugin cannot be found for the supplied readerInfoClass
	 *             name
	 */
	public String getReaderInfoAnnotation(String readerInfoClassName)
			throws RemoteException, RifidiReaderInfoNotFoundException;

	/**
	 * This returns the available commands and their groups for the supplied
	 * plugin.
	 * 
	 * @param readerInfoClassName
	 *            The qualified class name for the reader info of the desired
	 *            plugin
	 * @return A CommandGroupMap which contains information about the available
	 *         commands
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiReaderInfoNotFoundException
	 *             If a plugin cannot be found for the supplied readerInfoClass
	 *             name
	 */
	public List<CommandGroupMap> getAvaliableCommands(String readerInfoClassName)
			throws RemoteException, RifidiPluginDoesNotExistException;

	/**
	 * This method returns meta information about the available commands for the
	 * requested plugin. The meta information is returned in the dynamicSWTForm
	 * XML format.
	 * 
	 * @See org.rifidi.dynamicswtforms.xml.annotaions.Form
	 * @param readerInfoClassNameThe
	 *            qualified class name for the reader info of the desired plugin
	 * @return XML that is the meta information for the commands
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiPluginDoesNotExistException
	 *             If a plugin cannot be found for the supplied readerInfoClass
	 *             name
	 * @throws DynamicSWTFormAnnotationException
	 *             If there was a problem turning the Annotated classes into XML
	 */
	public String getCommandAnnotations(String readerInfoClassName)
			throws RemoteException, RifidiPluginDoesNotExistException,
			DynamicSWTFormAnnotationException;

	/**
	 * This method returns the available properties and thier groups for the
	 * supplied plugin
	 * 
	 * @param readerInfoClassName
	 *            The qualified class name for the reader info of the desired
	 *            plugin
	 * @return A CommandGroupMap which contains information about the available
	 *         properties
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiPluginDoesNotExistException
	 *             If a plugin cannot be found for the supplied readerInfoClass
	 *             nam
	 */
	public List<CommandGroupMap> getAvailableProperties(
			String readerInfoClassName) throws RemoteException,
			RifidiPluginDoesNotExistException;

	/**
	 * This method returns meta information about the available properties for
	 * the requested plugin. The meta information is returned in the
	 * dynamicSWTForm XML format.
	 * 
	 * @See org.rifidi.dynamicswtforms.xml.annotaions.Form
	 * @param readerInfoClassNameThe
	 *            qualified class name for the reader info of the desired plugin
	 * @return XML that is the meta information for the properties
	 * @throws RemoteException
	 *             If there was a network, serialization, or other RMI problem
	 * @throws RifidiPluginDoesNotExistException
	 *             If a plugin cannot be found for the supplied readerInfoClass
	 *             name
	 * @throws DynamicSWTFormAnnotationException
	 *             If there was a problem turning the Annotated classes into XML
	 */
	public String getPropertyAnnotations(String readerInfoClassName)
			throws RemoteException, DynamicSWTFormAnnotationException,
			RifidiPluginDoesNotExistException;

}
