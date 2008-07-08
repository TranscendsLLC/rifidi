package org.rifidi.edge.core.readersession.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.communication.service.ConnectionEventListener;
import org.rifidi.edge.core.communication.service.ConnectionService;
import org.rifidi.edge.core.exceptions.RifidiCommandInterruptedException;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.readersession.ExecutionListener;
import org.rifidi.edge.core.readersession.ReaderSessionJerry;
import org.rifidi.edge.core.readersession.ReaderSessionStatus;
import org.rifidi.services.annotations.Inject;

public class ReaderSessionImplJerry implements ReaderSessionJerry {

	Set<ExecutionListener> listeners = Collections.synchronizedSet(new HashSet<ExecutionListener>());
	
	private ConnectionService connectionService;
	
	private ReaderInfo readerInfo;
	
	private ConnectionManager connectionManager;
	
	private Connection connection;
	
	private ReaderSessionStatus status = ReaderSessionStatus.DISCONNECTED;

	private ReaderPluginService readerPluginService;

	private MessageService messageService;

	private Command commandInstance;
	
	public ReaderSessionImplJerry(ReaderInfo readerInfo){
		this.readerInfo = readerInfo;
		
	}
	
	@Override
	public void addExecutionListener(ExecutionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void executeCommand(String command) throws RifidiConnectionException, RifidiCommandInterruptedException {
		if (status == ReaderSessionStatus.BUSY) {
			throw new RifidiCommandInterruptedException("This reader is busy, please try this command again later.");
		}
		status = ReaderSessionStatus.BUSY;
		/*
		 * Life goes by a bit better with a bit of sanity checking...
		 */
		if (connectionService == null) 
			throw new RifidiConnectionException("No communication service.");
		
		if (readerPluginService == null)
			throw new RifidiConnectionException("No ReaderPlugin service.");
		
		if (messageService == null)
			throw new RifidiConnectionException("No Message service.");
		
		if (connectionManager == null)
			connectionManager = readerPluginService.getReaderPlugin(readerInfo.getClass()).getConnectionManager();
		
		insureConnection();
		
		/* look for and execute the command */
		for (Class<? extends Command> commandClass : readerPluginService.getReaderPlugin(readerInfo.getClass())
				.getAvailableCommands()) {
			System.out.println(commandClass.getName());

			CommandDesc commandDesc = commandClass
					.getAnnotation(CommandDesc.class);
			if (commandDesc == null) {
				throw new RifidiCommandInterruptedException("Command found.");
			} else {
				if (commandDesc.name().equals(command)) {
					try {
						Constructor<? extends Command> constructor = commandClass
								.getConstructor();

						
						commandInstance = (Command) constructor.newInstance();

						MessageQueue messageQueue = messageService
								.createMessageQueue();

						try {
							commandInstance.start(connection, messageQueue);
						} /* catch (IOException e) {
							insureConnection();
							// try one more time...
							try {
								commandInstance.start(connection, messageQueue);
							} catch (IOException e) {
								throw new RifidiConnectionException(
									"Error after exhausting " + 
									connectionManager.getMaxNumConnectionsAttemps() +
									"attempts. Probably have a unreliable connection" 
									,e
								);
							}
						}*/ catch (ClassCastException e) {
							throw new RifidiCommandInterruptedException(
									"Command interrupted due to unexpected ClassCastExecption. " + 
									"Probibly tried to execute a command that did not belong to this reader, " +  
									"or something else caused it.", e
							);
						} catch (RuntimeException e) {
							throw new RifidiCommandInterruptedException(
									"Command interrupted due to unexpected RuntimeException. " +
									"This may be caused by a bug in the command implemenation itself or, " +
									"or the protocol implemenation of the reader.", e
							);
						}
						
						
					} catch (SecurityException e) {
						throw new RifidiCommandInterruptedException("Command found.", e);
					} catch (NoSuchMethodException e) {
						throw new RifidiCommandInterruptedException("Command found.", e);
					} catch (IllegalArgumentException e) {
						throw new RifidiCommandInterruptedException("Command found.", e);
					} catch (InstantiationException e) {
						throw new RifidiCommandInterruptedException("Command found.", e);
					} catch (IllegalAccessException e) {
						throw new RifidiCommandInterruptedException("Command found.", e);
					} catch (InvocationTargetException e) {
						throw new RifidiCommandInterruptedException("Command found.", e);
					}
				}
			}
		}
		
		status = ReaderSessionStatus.CONNECTED;
		
		
	}

	private void insureConnection() throws RifidiConnectionException{
		
		if (connection != null) {
			try {
				for (int x = 1; connectionManager.getMaxNumConnectionsAttemps() <= x; x++ ) {
					try {
						connection = connectionService.createConnection(
								connectionManager, readerInfo);
					} catch (RifidiConnectionException e) {
						if (x == connectionManager.getMaxNumConnectionsAttemps()) {
							status = ReaderSessionStatus.DISCONNECTED;
							throw e;
						}
					}
				}
			} catch (RuntimeException e ) {
				throw new RifidiConnectionException(
						"RuntimeException detected! " +
						"There is a possible bug in " + 
						connectionManager.getClass().getName(), e);
			}
		}
		
	}
	
	
	@Override
	public ReaderInfo getReaderInfo() {
		return readerInfo;
	}

	@Override
	public ReaderSessionStatus getStatus() {
		// TODO Auto-generated method stub
		return status;
	}

	@Override
	public void removeExecutionListener(ExecutionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void stopCommand() {
		commandInstance.stop();
		commandInstance = null;
		status = ReaderSessionStatus.CONNECTED;
	}

	@Inject
	public void setConnectionService(ConnectionService connectionService) {
		this.connectionService = connectionService;
	}
	
	@Inject
	public void setReaderPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}
	
	@Inject
	public void setMessageService(MessageService messageService) {
		System.out.println("MessageService injected");
		this.messageService = messageService;
	}
}
