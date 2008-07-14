package org.rifidi.edge.core.rmi.readerconnection.impl;

import java.rmi.RemoteException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.rmi.readerconnection.RemoteReaderConnection;

/**
 * Implementation for a RemoteReaderConnection
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoteReaderConnectionImpl implements RemoteReaderConnection {

	private Log logger = LogFactory.getLog(RemoteReaderConnectionImpl.class);
	
	private ReaderSession readerSession;

	public RemoteReaderConnectionImpl(ReaderSession readerSession) {
		this.readerSession = readerSession;
	}

	@Override
	public String commandStatus(long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String commandStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String curExecutingCommand() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long curExecutingCommandID() throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long executeCommand(String command, String configuration)
			throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<String> getAvailableCommandGroups() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAvailableCommands() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getAvailableCommands(String groupName)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMessageQueueName() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReaderInfo getReaderInfo() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getReaderState() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resetReaderConnection() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long startTagStream(String configuration) throws RemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean stopCurCommand(boolean force) throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stopCurCommand(boolean force, long commandID)
			throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	public ReaderSession getReaderSession() {
		return readerSession;
	}

}
