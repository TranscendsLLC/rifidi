package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;
import org.w3c.dom.Document;

/**
 * The implementation of the RemoteReaderConnectionRegistry. This is the Factory
 * to create new RemoteReaderConnections. It will be exported by RMI to allow
 * Clients to create and delete RemoteReaderConnections. It also allows to keep
 * track of currently running RemoteReaderSessions.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoteReaderConnectionRegistryImpl implements
		RemoteReaderConnectionRegistry, ReaderSessionListener {

	private Log logger = LogFactory
			.getLog(RemoteReaderConnectionRegistryImpl.class);

	private ReaderPluginService readerPluginService;
	private ReaderSessionService readerSessionService;

	private HashMap<RemoteReaderConnection, RemoteReaderConnectionImpl> remoteSessionList = new HashMap<RemoteReaderConnection, RemoteReaderConnectionImpl>();
	private HashMap<ReaderSession, RemoteReaderConnection> readerSessionSync = new HashMap<ReaderSession, RemoteReaderConnection>();

	/**
	 * Constructor
	 */
	public RemoteReaderConnectionRegistryImpl() {
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public RemoteReaderConnection createReaderConnection(String connectionInfo)
			throws RemoteException, RifidiReaderInfoNotFoundException {
		try {
			List<String> readerInfoClassNames = readerPluginService
					.getAllReaderInfos();
			List<Class<?>> classRegister = new ArrayList<Class<?>>();
			for (String s : readerInfoClassNames) {
				classRegister.add(Class.forName(s));
			}

			JAXBContext context = JAXBContext.newInstance(classRegister
					.toArray(new Class[classRegister.size()]));

			Unmarshaller unmarshaller = context.createUnmarshaller();
			Reader reader = new StringReader(connectionInfo);
			ReaderInfo readerInfo = (ReaderInfo) unmarshaller.unmarshal(reader);
			ReaderSession readerSession = readerSessionService
					.createReaderSession(readerInfo);

			return saveRemoteConnection(readerSession, readerPluginService.getReaderPlugin(readerInfo.getClass().getName()));
		} catch (ClassNotFoundException e) {
			throw new RifidiReaderInfoNotFoundException(e.getMessage());
		} catch (JAXBException e) {
			throw new RifidiReaderInfoNotFoundException(e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry
	 * #deleteReaderConnection
	 * (org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection)
	 */
	@Override
	public void deleteReaderConnection(
			RemoteReaderConnection remoteReaderConnection)
			throws RemoteException {
		RemoteReaderConnectionImpl remoteConnection = remoteSessionList
				.remove(remoteReaderConnection);
		readerSessionService.destroyReaderSession(remoteConnection
				.getReaderSession());
		readerSessionSync.remove(remoteConnection.getReaderSession());
		UnicastRemoteObject.unexportObject(remoteConnection, true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry
	 * #getAllReaderConnections()
	 */
	@Override
	public List<RemoteReaderConnection> getAllReaderConnections()
			throws RemoteException {
		for (ReaderSession session : readerSessionService
				.getAllReaderSessions()) {
			if (!readerSessionSync.containsKey(session)) {
				saveRemoteConnection(session, readerPluginService.getReaderPlugin(session.getReaderInfo().getClass().getName()));
			}
		}
		return new ArrayList<RemoteReaderConnection>(remoteSessionList.keySet());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnectionRegistry
	 * #getAvailableReaderPlugins()
	 */
	@Override
	public List<String> getAvailableReaderPlugins() throws RemoteException {
		ArrayList<String> retVal = new ArrayList<String>(readerPluginService
				.getAllReaderInfos());
		return retVal;
	}

	@Override
	public String getReaderPluginXML(String readerInfoClassName) throws RifidiReaderPluginXMLNotFoundException {
		return readerPluginService.getReaderPlugin(readerInfoClassName).getReaderPluginXML();
	}

	@Override
	public String getReaderInfoAnnotation(String readerInfoClassName)
			throws RemoteException, RifidiReaderInfoNotFoundException,
			TransformerException {
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
		readerSessionService.addReaderSessionListener(this);
	}

	private RemoteReaderConnection saveRemoteConnection(
			ReaderSession readerSession, ReaderPlugin readerPlugin) {
		RemoteReaderConnectionImpl remoteReaderConnection = new RemoteReaderConnectionImpl(
				readerSession, readerPlugin);

		RemoteReaderConnection remoteReaderConnectionStub = null;
		try {
			remoteReaderConnectionStub = (RemoteReaderConnection) UnicastRemoteObject
					.exportObject(remoteReaderConnection, 0);
		} catch (RemoteException e) {
			e.printStackTrace();
			logger.error("Coudn't create RMI Stub for RemoteReaderConnection:",
					e);
			return null;
		}

		remoteSessionList.put(remoteReaderConnectionStub,
				remoteReaderConnection);
		readerSessionSync.put(readerSession, remoteReaderConnection);
		return remoteReaderConnectionStub;
	}

	@Override
	public void addReaderSessionEvent(ReaderSession readerSession) {
	}

	@Override
	public void removeReaderSessionEvent(ReaderSession readerSession) {
		RemoteReaderConnection remoteReaderConnection = readerSessionSync
				.remove(readerSession);
		remoteSessionList.values().remove(remoteReaderConnection);
	}

	@Override
	public void autoRemoveReaderSessionEvent(ReaderSession readerSession) {
		removeReaderSessionEvent(readerSession);
	}

	@Override
	public void readerInfoEditedEvent(ReaderInfo oldReaderInfo,
			ReaderInfo newReaderInfo) {
	}
}
