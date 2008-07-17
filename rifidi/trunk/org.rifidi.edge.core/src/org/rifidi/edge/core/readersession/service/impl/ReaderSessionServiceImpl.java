package org.rifidi.edge.core.readersession.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginListener;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.impl.ReaderSessionImpl;
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;

public class ReaderSessionServiceImpl implements ReaderSessionService,
		ReaderPluginListener {

	private ReaderPluginService readerPluginService;
	private ArrayList<ReaderSession> registry = new ArrayList<ReaderSession>();
	private ArrayList<ReaderSessionListener> listeners = new ArrayList<ReaderSessionListener>();
	private int sessionID = 1;

	private Log logger = LogFactory.getLog(ReaderSessionServiceImpl.class);
	
	public ReaderSessionServiceImpl(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
		readerPluginService.addReaderPluginListener(this);
	}

	@Override
	public ReaderSession createReaderSession(ReaderInfo readerInfo) {
		@SuppressWarnings("unused")
		ReaderPlugin readerPlugin = readerPluginService
				.getReaderPlugin(readerInfo.getClass());
		ReaderSession readerSession = new ReaderSessionImpl(readerInfo,
				sessionID++);
		registry.add(readerSession);

		for (ReaderSessionListener l : listeners) {
			l.addEvent(readerSession);
		}

		return readerSession;
	}

	@Override
	public void destroyReaderSession(ReaderSession readerSession) {
		logger.debug("removing reader session: " + readerSession);
		readerSession.stopCurCommand(true);
		if (readerSession instanceof ReaderSessionImpl) {
			ReaderSessionImpl readerSessionImpl = (ReaderSessionImpl) readerSession;
			readerSessionImpl.cleanUP();
		}
		registry.remove(readerSession);
		for (ReaderSessionListener l : listeners) {
			l.removeEvent(readerSession);
		}
	}

	@Override
	public List<ReaderSession> getAllReaderSessions() {
		return new ArrayList<ReaderSession>(registry);
	}

	@Override
	public void addReaderSessionListener(ReaderSessionListener listener) {
		listeners.add(listener);

	}

	@Override
	public void removeReaderSessionListener(ReaderSessionListener listener) {
		listeners.remove(listener);

	}

	@Override
	public void readerPluginRegisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {

	}

	@Override
	public void readerPluginUnregisteredEvent(
			Class<? extends ReaderInfo> readerInfo) {

		ArrayList<ReaderSession> tempReaderSessions = new ArrayList<ReaderSession>(
				this.registry);

		for (ReaderSession readerSession : tempReaderSessions) {
			if (readerSession.getReaderInfo().getClass().getName().equals(
					readerInfo.getName())) {
				logger.debug("removing reader session: " + readerSession);
				
				readerSession.stopCurCommand(true);
				if (readerSession instanceof ReaderSessionImpl) {
					ReaderSessionImpl readerSessionImpl = (ReaderSessionImpl) readerSession;
					readerSessionImpl.cleanUP();
				}
				if (registry.remove(readerSession)){
					logger.debug("Session Removed.");
				}
			}

		}

	}

}
