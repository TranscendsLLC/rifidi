package org.rifidi.edge.rmi.ReaderConnection.impl;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;

public class RemoteReaderConnectionImpl implements RemoteReaderConnection {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8838335635546640L;

	private Log logger = LogFactory.getLog(RemoteReaderConnectionRegistryImpl.class);

	private IReaderConnection readerConnection;

	public RemoteReaderConnectionImpl(IReaderConnection session) {
		this.readerConnection = session;
	}

	@Override
	public void sendCustomCommand(ICustomCommand customCommand)
			throws RemoteException {
		logger.debug("Remote Call: sendCustomCommand()");
		readerConnection.sendCustomCommand(customCommand);
	}

	@Override
	public void startTagStream() throws RemoteException {
		logger.debug("Remote Call: startTagStream()");
		readerConnection.startTagStream();
	}

	@Override
	public void stopTagStream() throws RemoteException {
		logger.debug("Remote Call: stopTagStream()");
		readerConnection.stopTagStream();
	}
	
	// TODO Think about a better place for this
	public IReaderConnection getSession() {
		return readerConnection;
	}

}
