package org.rifidi.edge.core.communication.simple.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.exceptions.RifidiInvalidMessageFormat;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class SimpleConnectionImpl implements Connection {
	private static final Log logger = LogFactory.getLog(SimpleConnectionImpl.class);

	private OutputStream outputStream;

	private InputStream inputStream;

	private CommunicationProtocol protocol;

	LinkedList<Object> recieved = new LinkedList<Object>();

	private ConnectionManager connectionManager;

	private ConnectionEventListener listener;

	public SimpleConnectionImpl(ConnectionManager connectionManager, ConnectionEventListener listener)  throws RifidiConnectionException {
		this.connectionManager = connectionManager;
		try {
			_connect();
		} catch (RifidiConnectionException e) {
			_reconnect();
		}
		this.listener = listener;
		if (this.listener != null)
			this.listener.connected();
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
			if (listener != null) {
				listener.connectionExceptionEvent(e);
				listener.disconnected();
			}
			disconnect();
			try {
				_reconnect();
			} catch (RifidiConnectionException e1) {
				if (listener != null) {
					listener.connectionExceptionEvent(e1);
					listener.error();
				}
			}
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
			if (listener != null) {
				listener.connectionExceptionEvent(e);
				listener.error();
			}
			disconnect();
			try {
				_reconnect();
			} catch (RifidiConnectionException e1) {
				if (listener != null) {
					listener.connectionExceptionEvent(e1);
					listener.error();
				}
			}
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

	/*===============================================================*/
	private void _reconnect() throws RifidiConnectionException {
		try {
			Thread.sleep(connectionManager.getReconnectionIntervall());
		} catch (InterruptedException e1) {
			// ignore this exception.
		}
		try {
			logger.debug("Trying to reconnect.");
			for (int x = 1; connectionManager.getMaxNumConnectionsAttemps() >= x; x++) {
				try {
					/* fire events */
					_connect();
					
				} catch (RifidiConnectionException e) {
					if (x == connectionManager.getMaxNumConnectionsAttemps()) {
						// status = ReaderSessionStatus.DISCONNECTED;
						// darn... we have failed.
						if (listener != null)
							listener.connectionExceptionEvent(e);
						logger.debug("Error! We gave up. " + e.getMessage());
						throw e;
					} else {
						logger.debug("Error! " + e.getMessage());
					}
					try {
						Thread.sleep(connectionManager
								.getReconnectionIntervall());
					} catch (InterruptedException e1) {
						// ignore this exception.
					}
					// we have failed... try again....
					continue;
				}
				// hay!!! we succeeded!!
				break;
			}
		} catch (RuntimeException e) {
			// status = ReaderSessionStatus.DISCONNECTED;
			throw new RifidiConnectionException("RuntimeException detected! "
					+ "There is a possible bug in "
					+ connectionManager.getClass().getName(), e);
		}
 		/* fire event */
		if (listener != null)
			listener.connected();
	}
	private void _connect() throws RifidiConnectionException {
		ConnectionStreams streams = this.connectionManager.createCommunication();
		this.outputStream = streams.getOutputStream();
		this.inputStream = streams.getInputStream();
		this.protocol = this.connectionManager.getCommunicationProtocol();
		this.connectionManager.connect(this);
	}
	
}
