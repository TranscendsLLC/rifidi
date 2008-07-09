package org.rifidi.edge.core.communication.impl;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.communication.threads.ReadThread;
import org.rifidi.edge.core.communication.threads.WriteThread;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionStreams;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class Communication implements ConnectionExceptionListener {
	
	private CommunicationProtocol protocol;
	private ConnectionManager connectionManager;

	private ConnectionImpl connection = null;

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	private ReadThread readThread;
	private WriteThread writeThread;

	private Set<ConnectionEventListener> listeners = new HashSet<ConnectionEventListener>();
	
	public Communication(ConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public Connection connect() throws RifidiConnectionException {

		readQueue = new LinkedBlockingQueue<Object>();
		writeQueue = new LinkedBlockingQueue<Object>();

		connection = new ConnectionImpl(readQueue, writeQueue);

		// Create physical connection
		try {
			_connect();
		} catch (RifidiConnectionException e) {
			reconnect();
		}


		// Create logical connection
		connectionManager.connect();
		
		/* fire events */
		for (ConnectionEventListener listener: listeners){
			listener.connected();
		}
		
		return connection;
	}

	public void disconnect() {
		// TODO make sure the order is fine
		
		/* fire events */
		for (ConnectionEventListener listener: listeners){
			listener.disconnected();
		}
		
		writeThread.ignoreExceptions(true);
		readThread.ignoreExceptions(true);

		connectionManager.disconnect();

		writeThread.stop();
		readThread.stop();
	}

	private void reconnect() throws RifidiConnectionException {
		/* fire events */
		for (ConnectionEventListener listener: listeners){
			listener.connected();
		}
		
		try {
			Thread.sleep(connectionManager.getReconnectionIntervall());
		} catch (InterruptedException e1) {
			// ignore this exception.
		}
		try {
			for (int x = 1; connectionManager.getMaxNumConnectionsAttemps() <= x; x++) {
				try {
					_connect();
				} catch (RifidiConnectionException e) {
					if (x == connectionManager
							.getMaxNumConnectionsAttemps()) {
						//status = ReaderSessionStatus.DISCONNECTED;
						//darn... we have failed.
						throw e;
					}
					try {
						wait(connectionManager.getReconnectionIntervall());
					} catch (InterruptedException e1) {
						// ignore this exception.
					}
					//we have failed... try again.... 
					continue;
				}
				//hay!!! we succeeded!! 
				break;
			}
		} catch (RuntimeException e) {
			connection = null;
			//status = ReaderSessionStatus.DISCONNECTED;
			throw new RifidiConnectionException(
					"RuntimeException detected! "
							+ "There is a possible bug in "
							+ connectionManager.getClass().getName(), e);
		} 	
		/* fire events */
		for (ConnectionEventListener listener: listeners){
			listener.disconnected();
		}
	}
	
	private void _connect() throws RifidiConnectionException{
		ConnectionStreams connectionStreams = connectionManager.createCommunication();
		readThread = new ReadThread(connectionManager.toString() + " Read", connection, protocol,
				readQueue, connectionStreams.getInputStream());

		writeThread = new WriteThread(connectionManager.toString() + " Write", connection, protocol,
				writeQueue, connectionStreams.getOutputStream());
		
		readThread.start();
		writeThread.start();
		
	}
	
	
	public void  addConnectionEventListener( ConnectionEventListener listener){
		listeners.add(listener);
		connection.addConnectionExceptionListener(listener);
	}
	
	public void  removeConnectionEventListener( ConnectionEventListener listener){
		listeners.add(listener);
		connection.addConnectionExceptionListener(listener);
	}
	
	/*==============================================================*/
	@Override
	public void finalize(){
		listeners.clear();
	}

	@Override
	public void connectionExceptionEvent(Exception exception) {
		// TODO Auto-generated method stub
		try {
			reconnect();
		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			/* fire events */
			for (ConnectionEventListener listener: listeners){
				listener.disconnectedOnError();
			}
		}
	}
	
}
