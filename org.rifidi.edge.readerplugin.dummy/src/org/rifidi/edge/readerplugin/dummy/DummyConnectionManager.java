package org.rifidi.edge.readerplugin.dummy;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
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

	/* used only when the dummy adapter is set to random errors */
	Random random;

	PipedInputStream input;
	PipedOutputStream output;
	
	 public DummyConnectionManager(ReaderInfo readerInfo) {
	 super(readerInfo);
		 // TODO Auto-generated constructor stub
		 this.info = (DummyReaderInfo) readerInfo;
	 }

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#connect()
	 */
	@Override
	public void connect() throws RifidiConnectionException {
		// always succeeds
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
		try {
			input.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			output.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#createCommunication()
	 */
	@Override
	public ConnectionStreams createCommunication()
			throws RifidiConnectionException {
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

		//TODO test this.
		input = new PipedInputStream();
		try {
			output = new PipedOutputStream(input);
		} catch (IOException e) {
			throw new RifidiConnectionException(e);
		}
		
		logger.debug("Connected");
		return new ConnectionStreams(input, output);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getMaxNumConnectionsAttemps()
	 */
	@Override
	public int getMaxNumConnectionsAttemps() {
		return (info.getMaxNumConnectionsAttemps() != 0) ? info.getMaxNumConnectionsAttemps() : 3;
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager#getReconnectionIntervall()
	 */
	@Override
	public long getReconnectionIntervall() {
		return (info.getReconnectionIntervall() != 0) ? info.getReconnectionIntervall() : 1000;
	}

	@Override
	public void stopKeepAlive() {
		//ignore this.

	}

}
