package org.rifidi.edge.rmi.ReaderConnection.impl;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;

public class RemoteReaderConnectionImpl implements RemoteReaderConnection {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8838335635546640L;

	private Log logger = LogFactory
			.getLog(RemoteReaderConnectionRegistryImpl.class);

	private IReaderConnection readerConnection;

	public RemoteReaderConnectionImpl(IReaderConnection readerConnection) {
		this.readerConnection = readerConnection;
	}

	@Override
	public void connect() throws RemoteException {
		logger.debug("RemoteCall: connect()");
		readerConnection.connect();
	}

	@Override
	public void disconnect() throws RemoteException {
		logger.debug("RemoteCall: disconnect()");
		readerConnection.disconnect();
	}

	@Override
	public void sendCustomCommand(ICustomCommand customCommand)
			throws RemoteException {
		logger.debug("RemoteCall: sendCustomCommand()");
		readerConnection.sendCustomCommand(customCommand);
	}

	@Override
	public void startTagStream() throws RemoteException {
		logger.debug("RemoteCall: startTagStream()");
		readerConnection.startTagStream();
	}

	@Override
	public void stopTagStream() throws RemoteException {
		logger.debug("RemoteCall: stopTagStream()");
		readerConnection.stopTagStream();
	}

	@Override
	public String getTagQueueName() throws RemoteException {
		logger.debug("RemoteCall: getTagQueueName()");
		return Integer.toString(readerConnection.getSessionID());
	}

	@Override
	public String getReaderState() throws RemoteException {
		logger.debug("RemoteCall: getReaderState()");
		return readerConnection.getState().toString();
	}

	@Override
	public AbstractReaderInfo getReaderInfo() throws RemoteException {
		logger.debug("RemoteCall: getReaderInfo()");
		return readerConnection.getConnectionInfo();
	}

}
