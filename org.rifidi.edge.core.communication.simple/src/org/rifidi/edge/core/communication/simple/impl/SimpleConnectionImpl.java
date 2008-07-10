package org.rifidi.edge.core.communication.simple.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidMessageFormat;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class SimpleConnectionImpl implements Connection {
	private static final Log logger = LogFactory.getLog(SimpleConnectionImpl.class);

	private Set<ConnectionExceptionListener> listeners = new HashSet<ConnectionExceptionListener>();

	private OutputStream outputStream;

	private InputStream inputStream;

	private CommunicationProtocol protocol;

	LinkedList<Object> recieved = new LinkedList<Object>();

	private ConnectionManager connectionManager;

	public SimpleConnectionImpl(ConnectionManager connectionManager)  throws RifidiConnectionException {
		this.connectionManager = connectionManager;
		ConnectionStreams streams = this.connectionManager.createCommunication();
		this.outputStream = streams.getOutputStream();
		this.inputStream = streams.getInputStream();
		this.protocol = this.connectionManager.getCommunicationProtocol();
		this.connectionManager.connect(this);
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.Connection#recieveMessage()
	 */
	@Override
	public Object recieveMessage() throws IOException {
		try{
			/* if there is nothing on the queue
			 * we block until there is something.
			 */
			if (recieved.isEmpty()) {
				_gatherMessagesBlocking();
			} else {
				/*
				 * if not... we just gather all or nothing... and go on.
				 */
				_gatherMessagesNonBlocking();
			}
			Object retVal = recieved.pop();
			
			return retVal;
		} catch (IOException e) {
			fireException(e);
			throw e;
		}
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.communication.Connection#sendMessage(java.lang.Object)
	 */
	@Override
	public void sendMessage(Object o) throws IOException {
		try {
			logger.debug(o);
			byte[] bytes;
			try {
				bytes = protocol.messageToByte(o);
			} catch (RifidiInvalidMessageFormat e) {
				throw new IOException(e);
			}

			outputStream.write(bytes);
			outputStream.flush();

			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// Ignore this.
			}

			//Store any available messages on a queue and don't block.
			_gatherMessagesNonBlocking();
			
		} catch (IOException e) {
			fireException(e);
			throw e;
		}
	}

	/**
	 * @param blocking 
	 * @throws IOException
	 * @throws RifidiInvalidMessageFormat
	 */
	private void _gatherMessagesNonBlocking() throws IOException {
		Object message = null;
		while (inputStream.available() > 0) {
			int input = inputStream.read();
			try {
				message = protocol.byteToMessage((byte) input);
			} catch (RifidiInvalidMessageFormat e) {
				throw new IOException(e);
			}
			if (message != null) {
				logger.debug(message);
				recieved.push(message);
			}
		}
	}
	
	private void _gatherMessagesBlocking() throws IOException {
		Object message = null;
		int input;
		while ((input = inputStream.read()) != -1) {
			try {
				message = protocol.byteToMessage((byte) input);
			} catch (RifidiInvalidMessageFormat e) {
				throw new IOException(e);
			}
			if (message != null) {
				logger.debug(message);
				recieved.push(message);
			}
		}
	}
	

	public void disconnect(){
		connectionManager.disconnect(this);
	}
	
	@Override
	public boolean isMessageAvailable() throws IOException {
		return !recieved.isEmpty();
	}

	public void fireException(Exception e) {
		for (ConnectionExceptionListener listener : listeners) {
			listener.connectionExceptionEvent(e);
		}
	}

	public void addConnectionExceptionListener(
			ConnectionExceptionListener listener) {
		listeners.add(listener);
	}

	public void removeConnectionExceptionListener(
			ConnectionExceptionListener listener) {
		listeners.add(listener);
	}

	/* ============================================================== */
	@Override
	public void finalize() {
		listeners.clear();
	}
}
