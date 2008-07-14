package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.rmi.RemoteException;

import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;
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
	public ReaderInfo getReaderInfo() throws RemoteException {
		return readerSession.getReaderInfo();
	}

	@Override
	public String getReaderState() throws RemoteException {
		return readerSession.getStatus().toString();
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
