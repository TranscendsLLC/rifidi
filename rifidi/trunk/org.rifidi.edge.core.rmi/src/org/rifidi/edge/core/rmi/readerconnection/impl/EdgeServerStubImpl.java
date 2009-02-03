package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.io.Reader;
import java.io.StringReader;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.server.Unreferenced;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;
import org.rifidi.edge.core.api.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;
import org.rifidi.edge.core.rmi.api.readerconnection.EdgeServerStub;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderSessionStub;
import org.rifidi.edge.core.rmi.service.RMIServerService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * The implementation of the EdgeServerStub. This is the Factory to create new
 * RemoteReaderConnections. It will be exported by RMI to allow Clients to
 * create and delete RemoteReaderConnections. It also allows to keep track of
 * currently running RemoteReaderSessions.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class EdgeServerStubImpl extends UnicastRemoteObject implements
		EdgeServerStub, ReaderSessionListener, Unreferenced {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Log logger = LogFactory.getLog(EdgeServerStubImpl.class);

	private ReaderPluginService readerPluginService;
	private ReaderSessionService readerSessionService;
	private RMIServerService rmiServerService;

	/**
	 * Constructor
	 */
	public EdgeServerStubImpl() throws RemoteException {
		super();
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public Long createReaderSession(String xml) throws RemoteException,
			RifidiReaderInfoNotFoundException {
		logger.debug("RMI Call: createReaderConnection");

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
			Reader reader = new StringReader(xml);
			ReaderInfo readerInfo = (ReaderInfo) unmarshaller.unmarshal(reader);

			ReaderSession readerSession = readerSessionService
					.createReaderSession(readerInfo);

			saveRemoteConnection(readerSession);

			return readerSession.getSessionID();
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
	 * org.rifidi.edge.core.rmi.api.readerconnection.RemoteReaderConnectionRegistry
	 * #deleteReaderConnection
	 * (org.rifidi.edge.core.rmi.api.readerconnection.RemoteReaderConnection)
	 */
	@Override
	public void deleteReaderSession(Long ID) throws RemoteException {
		logger.debug("RMI Call: deleteReaderConnection");
		ReaderSession session = readerSessionService.getReaderSession(ID);
		if (null != session) {
			readerSessionService.destroyReaderSession(session);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.rmi.api.readerconnection.RemoteReaderConnectionRegistry
	 * #getAllReaderConnections()
	 */
	@Override
	public Set<Long> getAllReaderSessions() throws RemoteException {
		logger.debug("RMI Call: getAllReaderConnections");
		HashSet<Long> set = new HashSet<Long>();
		for (ReaderSession session : readerSessionService
				.getAllReaderSessions()) {
			set.add(session.getSessionID());
			// TODO: do I need to check to see if the session has been exported?

		}
		return set;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.rmi.readerconnection.EdgeServerStub#
	 * getReaderSessionStubs(java.util.Set)
	 */
	@Override
	public HashMap<Long, ReaderSessionStub> getReaderSessionStubs(
			Set<Long> readerSessionIds) throws RemoteException {
		HashMap<Long, ReaderSessionStub> stubs = new HashMap<Long, ReaderSessionStub>();
		for (Long id : readerSessionIds) {
			Remote obj = rmiServerService.lookup(Long.toString(id));
			if (obj != null) {
				if (obj instanceof ReaderSessionStub) {
					stubs.put(id, (ReaderSessionStub) obj);
				}
			}
		}
		return stubs;
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

	@Inject
	public void setRMIServerService(RMIServerService rmiServerService) {
		this.rmiServerService = rmiServerService;
	}

	private ReaderSessionStub saveRemoteConnection(ReaderSession readerSession)
			throws RemoteException {
		ReaderSessionStubImpl sessionStub = new ReaderSessionStubImpl(
				readerSession);

		this.rmiServerService.bindToRMI(sessionStub, Long
				.toString(readerSession.getSessionID()));
		return sessionStub;
	}

	@Override
	public void addReaderSessionEvent(ReaderSession readerSession) {
		try {
			saveRemoteConnection(readerSession);
		} catch (RemoteException e) {
			logger.error("Cannot export object with ID "
					+ readerSession.getSessionID());
		}
	}

	@Override
	public void removeReaderSessionEvent(ReaderSession readerSession) {
		try {
			this.rmiServerService.unbindFromRMI(Long.toString(readerSession
					.getSessionID()));
		} catch (RemoteException e) {
			logger.error("Cannot unexport object with ID "
					+ readerSession.getSessionID());
		}

	}

	@Override
	public void autoRemoveReaderSessionEvent(ReaderSession readerSession) {
		removeReaderSessionEvent(readerSession);
	}

	@Override
	public void readerInfoEditedEvent(ReaderInfo oldReaderInfo,
			ReaderInfo newReaderInfo) {
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
