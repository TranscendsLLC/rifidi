package org.rifidi.edge.core.readersession.impl;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.communication.service.ConnectionService;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ExecutionListener;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.ReaderSessionStatus;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class ReaderSessionImpl2 implements ReaderSession,
		ConnectionEventListener {

	private ReaderSessionStatus status = ReaderSessionStatus.DISCONNECTED;
	long timeout = 1000;
	boolean isInitialized = false;
	
	private ConnectionService connectionService;
	private ReaderPluginService readerPluginService;
	
	private ConnectionManager connectionManager;
	private Connection connection;
	private ReaderInfo readerInfo;

	public ReaderSessionImpl2(ReaderInfo readerInfo ) {
		this.readerInfo = readerInfo;
		ServiceRegistry.getInstance().service(this);
	}
	
	@Override
	public void addExecutionListener(ExecutionListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeCommand(String command) throws RifidiConnectionException {
		if (status == ReaderSessionStatus.CONNECTED) {
			// lookup Command
			// if(command is valid)
			// set state, execute, unset state
//		} else if (status == ReaderSessionStatus.BUSY) {
//			throw new RifidiReaderSessionBusyException();
//		} else if (status == ReaderSessionStatus.ERROR) {
//			throw new RifidiReaderSessionErrorException();
		} else {
			if (!isInitialized) {
				
				ReaderPlugin readerPlugin = readerPluginService.getReaderPlugin(readerInfo.getClass());
				
				connectionManager = readerPlugin.getConnectionManager();

				connectionManager.addConnectionEventListener(this);
				
				connection = connectionService.createConnection(
						connectionManager, readerInfo);
				isInitialized=true;
				executeCommand(command);
				
				
			} else {
//				this.wait(timeout);
				if(this.status == ReaderSessionStatus.CONNECTED){
					executeCommand(command);
				}
			}

		}

	}

	@Override
	public ReaderInfo getReaderInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ReaderSessionStatus getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeExecutionListener(ExecutionListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void stopCommand() {
		// TODO Auto-generated method stub

	}

	@Override
	public void connected() {
		this.status = ReaderSessionStatus.CONNECTED;
		this.notify();

	}

	@Override
	public void disconnected() {
		this.status = ReaderSessionStatus.DISCONNECTED;
		this.notify();

	}

	@Inject
	public void setConnectionService(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}

}
