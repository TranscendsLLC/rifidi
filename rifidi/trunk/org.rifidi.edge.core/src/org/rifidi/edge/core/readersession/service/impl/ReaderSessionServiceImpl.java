package org.rifidi.edge.core.readersession.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.impl.ReaderSessionImpl;
import org.rifidi.edge.core.readersession.service.ReaderSessionListener;
import org.rifidi.edge.core.readersession.service.ReaderSessionService;

public class ReaderSessionServiceImpl implements ReaderSessionService {

	private ReaderPluginService readerPluginService;
	private ArrayList<ReaderSession> registry = new ArrayList<ReaderSession>();
	private ArrayList<ReaderSessionListener> listeners = new ArrayList<ReaderSessionListener>();
	private int sessionID=1;

	public ReaderSessionServiceImpl(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

	@Override
	public ReaderSession createReaderSession(ReaderInfo readerInfo) {
		@SuppressWarnings("unused")
		ReaderPlugin readerPlugin = readerPluginService
				.getReaderPlugin(readerInfo.getClass());
		ReaderSession readerSession = new ReaderSessionImpl(readerInfo, sessionID++);
		registry.add(readerSession);
		
		for(ReaderSessionListener l : listeners){
			l.addEvent(readerSession);
		}
		
		return readerSession;
	}

	@Override
	public void destroyReaderSession(ReaderSession readerSession) {
		readerSession.stopCurCommand(true);
		if (readerSession instanceof ReaderSessionImpl) {
			ReaderSessionImpl readerSessionImpl = (ReaderSessionImpl) readerSession;
			readerSessionImpl.cleanUP();
		}
		registry.remove(readerSession);
		for(ReaderSessionListener l : listeners){
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

}
