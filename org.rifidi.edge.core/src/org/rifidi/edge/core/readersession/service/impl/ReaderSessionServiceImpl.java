package org.rifidi.edge.core.readersession.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;
import org.rifidi.edge.core.api.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.api.readerplugin.service.ReaderPluginListener;
import org.rifidi.edge.core.api.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.impl.states.ReaderSessionImpl;
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;

/**
 * This is the implementation of a ReaderSessionService. It allows to create new
 * ReaderSessions, as well as the removal of a previous created ReaderSession.
 * It's also used as a Registry to keep track of the currently available
 * ReaderSessions. It supports event monitoring to be able to get notified when
 * a new ReaderSession is created or a ReaderSession get removed.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ReaderSessionServiceImpl implements ReaderSessionService,
		ReaderPluginListener {

	private ReaderPluginService readerPluginService;
	private ConcurrentHashMap<Long, ReaderSession> registry = new ConcurrentHashMap<Long, ReaderSession>();

	private Collection<ReaderSessionListener> listeners = new HashSet<ReaderSessionListener>();
	private long sessionID = 0;

	private Log logger = LogFactory.getLog(ReaderSessionServiceImpl.class);

	/**
	 * Create a new ReaderSessionService
	 * 
	 * @param readerPluginService
	 *            the ReaderPluginService necessary to create different reader
	 *            types
	 */
	public ReaderSessionServiceImpl(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
		readerPluginService.addReaderPluginListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.service.ReaderSessionService#
	 * createReaderSession(org.rifidi.edge.core.readerplugin.ReaderInfo)
	 */
	@Override
	public ReaderSession createReaderSession(ReaderInfo readerInfo) {
		ReaderPlugin plugin = readerPluginService.getReaderPlugin(readerInfo.getClass()
				.getName());
		sessionID = sessionID + 1;
		ReaderSession readerSession = new ReaderSessionImpl(plugin, readerInfo,
				sessionID);
		registry.put(sessionID, readerSession);
		logger.debug("Created new session: " + readerSession);

		for (ReaderSessionListener l : listeners) {
			l.addReaderSessionEvent(readerSession);
		}

		return readerSession;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.service.ReaderSessionService#
	 * destroyReaderSession(org.rifidi.edge.core.readersession.ReaderSession)
	 */
	@Override
	public void destroyReaderSession(ReaderSession readerSession) {
		logger.debug("removing reader session: " + readerSession);
		readerSession.stopCurCommand(true);
		if (readerSession instanceof ReaderSessionImpl) {
			ReaderSessionImpl readerSessionImpl = (ReaderSessionImpl) readerSession;
			readerSessionImpl.destroy();
		}
		ReaderSession session = registry.remove(readerSession.getSessionID());
		if (null==session) {
			logger.debug("Reader session not removed: " + readerSession);
		}
		for (ReaderSessionListener l : listeners) {
			l.removeReaderSessionEvent(readerSession);
		}
	}
	
	@Override
	public void modifyReaderInfo(ReaderSession readerSession, ReaderInfo newReaderInfo){
		logger.debug("modifying readerInfo for " + readerSession);
		ReaderInfo oldReaderInfo = readerSession.getReaderInfo();
		boolean success = readerSession.setReaderInfo(newReaderInfo);
		if(success){
			for(ReaderSessionListener l : listeners){
				l.readerInfoEditedEvent(oldReaderInfo, newReaderInfo);
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readersession.service.ReaderSessionService#getReaderSession(long)
	 */
	@Override
	public ReaderSession getReaderSession(long id) {
		return this.registry.get(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.service.ReaderSessionService#
	 * getAllReaderSessions()
	 */
	@Override
	public List<ReaderSession> getAllReaderSessions() {
		return new ArrayList<ReaderSession>(registry.values());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.service.ReaderSessionService#
	 * addReaderSessionListener
	 * (org.rifidi.edge.core.readersession.service.ReaderSessionListener)
	 */
	@Override
	public void addReaderSessionListener(ReaderSessionListener listener) {
		listeners.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readersession.service.ReaderSessionService#
	 * removeReaderSessionListener
	 * (org.rifidi.edge.core.readersession.service.ReaderSessionListener)
	 */
	@Override
	public void removeReaderSessionListener(ReaderSessionListener listener) {
		listeners.remove(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginListener#
	 * readerPluginRegisteredEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginRegisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginListener#
	 * readerPluginUnregisteredEvent(java.lang.Class)
	 */
	@Override
	public void readerPluginUnregisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {

		ArrayList<ReaderSession> tempReaderSessions = new ArrayList<ReaderSession>(
				this.registry.values());

		for (ReaderSession readerSession : tempReaderSessions) {
			if (readerSession.getReaderInfo().getClass().getName().equals(
					readerInfo.getName())) {
				logger.debug("removing reader session: " + readerSession);

				readerSession.stopCurCommand(true);
				if (readerSession instanceof ReaderSessionImpl) {
					ReaderSessionImpl readerSessionImpl = (ReaderSessionImpl) readerSession;
					readerSessionImpl.destroy();
				}
				if (registry.remove(readerSession.getSessionID())!=null) {
					logger.debug("Session Removed.");
					for (ReaderSessionListener l : listeners) {
						l.autoRemoveReaderSessionEvent(readerSession);
					}

				}
			}

		}

	}

}
