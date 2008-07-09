package org.rifidi.edge.rmi.ReaderConnection.impl;

import java.rmi.RemoteException;

import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;
import org.rifidi.edge.rmi.customcommand.CustomCommand;

/**
 * Implementation for a RemoteReaderConnection
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoteReaderConnectionImpl implements RemoteReaderConnection {

	private ReaderSession readerSession;

	public RemoteReaderConnectionImpl(ReaderSession readerSession) {
		this.readerSession = readerSession;
	}

	@Override
	public void connect() throws RemoteException {
		// Ignore because of the new connection handling

	}

	@Override
	public void disconnect() throws RemoteException {
		// Ignore because of the new connection handling

	}

	@Override
	public ReaderInfo getReaderInfo() throws RemoteException {
		return readerSession.getReaderInfo();
	}

	@Override
	public String getReaderState() throws RemoteException {
		return readerSession.getStatus().toString();
	}

	@Override
	public String getTagQueueName() throws RemoteException {
		// TODO Readersession doesn't provide the QueueName yet
		return null;
	}

	@Override
	public void sendCustomCommand(CustomCommand customCommand)
			throws RemoteException {
		// TODO Auto-generated method stub
	}

	@Override
	public void startTagStream() throws RemoteException {
		try {
			readerSession.executeCommand("tagstreaming");
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			throw new RemoteException("Exception occured", e);
		} catch (RifidiCommandInterruptedException e) {
			e.printStackTrace();
			throw new RemoteException("Exception occured", e);
		}
	}

	@Override
	public void stopTagStream() throws RemoteException {
		readerSession.stopCommand();
	}

	public ReaderSession getReaderSession() {
		return readerSession;
	}

}
