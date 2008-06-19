package org.rifidi.edge.rmi.ReaderConnection.impl;

import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.exception.RifidiException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPlugin.commands.ICustomCommand;
import org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection;

/**
 * This is the Implementation of the RemoteReaderConnection. It allows to
 * control a ReaderConnection over RMI.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RemoteReaderConnectionImpl implements RemoteReaderConnection {

	/**
	 * SerialVersionID needed by the Serializeable Interface
	 */
	private static final long serialVersionUID = 8838335635546640L;

	private Log logger = LogFactory
			.getLog(RemoteReaderConnectionRegistryImpl.class);

	/**
	 * Instance of the internal ReaderConnection. The RemoteReaderConnection is
	 * used as a Proxy to avoid the need of throwing RemoteException
	 */
	private IReaderConnection readerConnection;

	/**
	 * Takes in the ReaderConnection this RemoteReaderConnection is associated
	 * with.
	 * 
	 * @param readerConnection
	 */
	public RemoteReaderConnectionImpl(IReaderConnection readerConnection) {
		this.readerConnection = readerConnection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection#connect()
	 */
	@Override
	public void connect() throws RemoteException {
		logger.debug("RemoteCall: connect()");
		try {
			readerConnection.connect();
		} catch (RifidiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RemoteException("Error calling Remote method.",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection#disconnect()
	 */
	@Override
	public void disconnect() throws RemoteException {
		logger.debug("RemoteCall: disconnect()");
		try {
			readerConnection.disconnect();
		} catch (RifidiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RemoteException("Error calling Remote method.",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection#sendCustomCommand(org.rifidi.edge.core.readerPlugin.commands.ICustomCommand)
	 */
	@Override
	public void sendCustomCommand(ICustomCommand customCommand)
			throws RemoteException {
		logger.debug("RemoteCall: sendCustomCommand()");
		try {
			readerConnection.sendCustomCommand(customCommand);
		} catch (RifidiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RemoteException("Error calling Remote method.",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection#startTagStream()
	 */
	@Override
	public void startTagStream() throws RemoteException {
		logger.debug("RemoteCall: startTagStream()");
		try {
			readerConnection.startTagStream();
		} catch (RifidiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RemoteException("Error calling Remote method.",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection#stopTagStream()
	 */
	@Override
	public void stopTagStream() throws RemoteException {
		logger.debug("RemoteCall: stopTagStream()");
		try {
			readerConnection.stopTagStream();
		} catch (RifidiException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			throw new RemoteException("Error calling Remote method.",e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection#getTagQueueName()
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.rmi.ReaderConnection.RemoteReaderConnection#getReaderInfo()
	 */
	@Override
	public AbstractReaderInfo getReaderInfo() throws RemoteException {
		logger.debug("RemoteCall: getReaderInfo()");
		return readerConnection.getConnectionInfo();
	}

}
