package org.rifidi.edge.readerplugin.dummy;

import java.util.Collections;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;
import org.rifidi.edge.readerplugin.dummy.protocol.DummyCommunicationProtocol;

public class DummyConnectionManager extends ConnectionManager {
	private static final Log logger = LogFactory
			.getLog(DummyConnectionManager.class);
	DummyReaderInfo info;

	Set<ConnectionEventListener> listeners = Collections
			.synchronizedSet(new HashSet<ConnectionEventListener>());

	/* used only when the dummy adapter is set to random errors */
	Random random;

	// public DummyConnectionManager(ReaderInfo readerInfo) {
	// super(readerInfo);
	// // TODO Auto-generated constructor stub
	// this.info = (DummyReaderInfo) readerInfo;
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#connect()
	 */
	@Override
	public void connect(ReaderInfo readerInfo) throws RifidiConnectionException {
		readerInfo = (DummyReaderInfo) readerInfo;
		/* used for breakage testing purposes */
		switch (info.getErrorToSet()) {
		case CONNECT:
			throw new RifidiConnectionException();
		case CONNECT_RUNTIME:
			throw new RuntimeException();
		case RANDOM:
			if (info.getRandom().nextDouble() <= info
					.getRandomErrorProbibility()) {
				if (info.getRandom().nextDouble() <= info
						.getProbiblityOfErrorsBeingRuntimeExceptions()) {
					throw new RuntimeException();
				} else {
					throw new RifidiConnectionException();
				}
			}
		}

		logger.debug("Connected");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#disconnect()
	 */
	@Override
	public void disconnect() {
		/* used for breakage testing purposes */
		switch (info.getErrorToSet()) {
		case DISCONNECT:
			// throw new RifidiConnectionException();
		case DISCONNECT_RUNTIME:
			throw new RuntimeException();
		case RANDOM:
			if (info.getRandom().nextDouble() <= info
					.getRandomErrorProbibility()) {
				if (info.getRandom().nextDouble() <= info
						.getProbiblityOfErrorsBeingRuntimeExceptions()) {
					throw new RuntimeException();
				} else {
					// throw new RifidiConnectionException();
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getCommunicationProtocol()
	 */
	@Override
	public CommunicationProtocol getCommunicationProtocol() {
		return new DummyCommunicationProtocol(info);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#startKeepAlive()
	 */
	@Override
	public void startKeepAlive() {
		// do nothing
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#addConnectionEventListener(org.rifidi.edge.core.communication.service.ConnectionEventListener)
	 */
	@Override
	public void addConnectionEventListener(ConnectionEventListener event) {
		listeners.add(event);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#createCommunication()
	 */
	@Override
	public ConnectionStreams createCommunication(ReaderInfo readerInfo)
			throws RifidiConnectionException {
		readerInfo = (DummyReaderInfo) readerInfo;
		return null;
	}

	@Override
	public int getMaxNumConnectionsAttemps() {
		// TODO Auto-generated method stub
		return 100;
	}

	@Override
	public long getReconnectionIntervall() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void removeConnectionEventListener(ConnectionEventListener event) {
		listeners.remove(event);
	}

	@Override
	public void stopKeepAlive() {
		// TODO Auto-generated method stub

	}

	@Override
	public void connectionExceptionEvent(Exception exception) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fireEvent() {
		// TODO Auto-generated method stub
		
	}

}
