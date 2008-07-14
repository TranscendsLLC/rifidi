package org.rifidi.edge.core.communication.simple.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.CommunicationStateListener;
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

	private CommunicationStateListener listener;

	public SimpleConnectionImpl(ConnectionManager connectionManager, CommunicationStateListener listener)  throws RifidiConnectionException {
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

			Object retVal = null;
			//TODO: Deal if we get things back faster than we can receive them.
			while (true) {
				int input;
				/*
				 * if there is one or more objects on the buffer.
				 */
				if (!recieved.isEmpty()) {
					/*
					 * check to see if there is any bytes
					 * available on the inputStream
					 * if not... we break the while loop.
					 */
					if ( !(inputStream.available() > 0) ) break;
				}
				/*
				 * Do a blocking read for one byte.
				 */	
				if ((input = inputStream.read()) == -1) throw new IOException();

				/* 
				 * send that byte to the protocol
				 */
				retVal = protocol.byteToMessage((byte) input);
				
				/* 
				 * check to see if the protocol sent us a complete message.
				 */
				if (retVal != null) {
					/*
					 * if so.. we push it on the stack.
					 */
					recieved.push(retVal);
				}
			}
		} catch (IOException e) {
			if (listener != null) {
				listener.disconnected();
			}
			disconnect();
			try {
				_reconnect();
			} catch (RifidiConnectionException e1) {
				if (listener != null) {
					listener.error();
				}
			}
			throw e;
		} catch (RifidiInvalidMessageFormat e) {
			if (listener != null) {
				listener.error();
			}
			throw new IOException(e);
		}
		
		/* 
		 * pop just one object off the stack.		 * 
		 */
		return recieved.pop();
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
	
		} catch (IOException e) {
			if (listener != null) {
				listener.error();
			}
			disconnect();
			try {
				_reconnect();
			} catch (RifidiConnectionException e1) {
				if (listener != null) {
					listener.error();
				}
			}
			throw e;
		}
	}

	public void disconnect(){
		connectionManager.disconnect(this);
		recieved.clear();
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
					disconnect();
					if (x == connectionManager.getMaxNumConnectionsAttemps()) {
						// status = ReaderSessionStatus.DISCONNECTED;
						// darn... we have failed.
						if (listener != null)
							listener.error();
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
