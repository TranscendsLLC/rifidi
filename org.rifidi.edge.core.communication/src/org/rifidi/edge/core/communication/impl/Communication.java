package org.rifidi.edge.core.communication.impl;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.RifidiConnectionException;
import org.rifidi.edge.core.communication.threads.ReadThread;
import org.rifidi.edge.core.communication.threads.WriteThread;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.protocol.CommunicationProtocol;

public class Communication implements ConnectionExceptionListener {

	private ReaderInfo readerInfo;
	private CommunicationProtocol protocol;
	private ConnectionManager connectionManager;

	private Socket socket = null;
	private ConnectionImpl connection = null;

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	private ReadThread readThread;
	private WriteThread writeThread;

	private int remainingReconnectionAttempts = 0;
	private long waitBetweenReconnect = 0;

	public Communication(ConnectionManager connectionManager,
			ReaderInfo readerInfo) {
		this.connectionManager = connectionManager;
		this.readerInfo = readerInfo;
		this.protocol = connectionManager.getCommunicationProtocol();
	}

	public Connection connect() throws RifidiConnectionException {

		// TODO do checking for null arguments
		String hostname = readerInfo.getIpAddress();
		int port = readerInfo.getPort();

		// Create Socket
		try {
			socket = new Socket(hostname, port);
		} catch (UnknownHostException e) {
			attemptReconnection();
		} catch (IOException e) {
			attemptReconnection();
		}

		// Create read and write Queue
		readQueue = new LinkedBlockingQueue<Object>();
		writeQueue = new LinkedBlockingQueue<Object>();

		connection = new ConnectionImpl(readQueue, writeQueue);

		try {
			readThread = new ReadThread("threadname", connection, protocol,
					readQueue, socket.getInputStream());

			writeThread = new WriteThread("threadname", connection, protocol,
					writeQueue, socket.getOutputStream());
		} catch (IOException e) {
			attemptReconnection();
		}

		readThread.start();
		writeThread.start();

		connection.addConnectionExceptionListener(this);

		// create logical connection
		connectionManager.connect(readerInfo, connection);
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

		connectionManager.reconnect();
	}

	//TODO: fix later
	private void attemptReconnection() throws RifidiConnectionException {
		// TODO do checking for null arguments
		String hostname = readerInfo.getIpAddress();
		int port = readerInfo.getPort();

		while (remainingReconnectionAttempts >= 0) {

			try {
				Thread.sleep(waitBetweenReconnect);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				socket = new Socket(hostname, port);
				readThread = new ReadThread("threadname", connection, protocol,
						readQueue, socket.getInputStream());

				writeThread = new WriteThread("threadname", connection,
						protocol, writeQueue, socket.getOutputStream());
				readThread.start();
				writeThread.start();
			} catch (UnknownHostException e) {
				remainingReconnectionAttempts--;
			} catch (IOException e) {
				remainingReconnectionAttempts--;
			}

		}

	}

}
