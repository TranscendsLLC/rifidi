package org.rifidi.edge.core.communication.impl;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.communication.Connection;
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

	private Socket socket = null;
	private ConnectionImpl connection = null;

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	private ReadThread readThread;
	private WriteThread writeThread;

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

		connection.addConnectionExceptionListener(this);

		return connection;
	}

	public void disconnect() {
		// TODO make sure the order is fine

		writeThread.ignoreExceptions(true);
		readThread.ignoreExceptions(true);

		connectionManager.disconnect();

		writeThread.stop();
		readThread.stop();
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Ignore this exception
			e.printStackTrace();
		}
	}

	@Override
	public void connectionExceptionEvent(Exception exception) {

		// connectionManager.reconnect();
	}

	private void reconnect() throws RifidiConnectionException {

		int remainingConnectionAttempts =connectionManager.getMaxNumConnectionsAttemps();
		
		while (remainingConnectionAttempts>=0) {
			
			try {
				Thread.sleep(connectionManager.getReconnectionIntervall());
			} catch (InterruptedException e) {
				// Ignore this
			}

			try {
				_connect();
				break;
			} catch (RifidiConnectionException e) {
				//TODO put logging message in here
				remainingConnectionAttempts --;
			}
			
			
		}
		if(remainingConnectionAttempts==0){
			throw new RifidiConnectionException("Max number of reconnect attempts exceeded");
		}

	}
	
	private void _connect() throws RifidiConnectionException{
		ConnectionStreams connectionStreams = connectionManager.createCommunication();
		readThread = new ReadThread("threadname", connection, protocol,
				readQueue, connectionStreams.getInputStream());

		writeThread = new WriteThread("threadname", connection, protocol,
				writeQueue, connectionStreams.getOutputStream());
		
		readThread.start();
		writeThread.start();
		
	}
}
