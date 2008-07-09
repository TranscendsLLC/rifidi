package org.rifidi.edge.core.readersession.impl;

import java.io.IOException;
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
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.ReaderSessionStatus;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public class ReaderSessionImplJe implements ReaderSession, ConnectionEventListener {
	private Set<ExecutionListener> listeners = Collections
			.synchronizedSet(new HashSet<ExecutionListener>());

	private ConnectionService connectionService;

	private ReaderInfo readerInfo;

	private ConnectionManager connectionManager;

	private Connection connection;

	private ReaderSessionStatus status = ReaderSessionStatus.DISCONNECTED;

	private ReaderPluginService readerPluginService;

	private MessageService messageService;

	private Command commandInstance;
	
	private MessageQueue messageQueue;

	public ReaderSessionImplJe(ReaderInfo readerInfo) {
		this.readerInfo = readerInfo;
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public void addExecutionListener(ExecutionListener listener) {
		listeners.add(listener);
	}

	//TODO Implement firing of events.
	@Override
	public void executeCommand(String command)
			throws RifidiConnectionException, RifidiCommandInterruptedException {
		if (status == ReaderSessionStatus.BUSY) {
			throw new RifidiCommandInterruptedException(
					"This reader is busy, please try this command again later.");
		}
		status = ReaderSessionStatus.BUSY;
		/*
		 * Life goes by a bit better with a bit of sanity checking...
		 */
		if (connectionService == null){
			status = ReaderSessionStatus.CONNECTED;
			throw new RifidiConnectionException("No communication service.");
		}
			
		if (readerPluginService == null){
			status = ReaderSessionStatus.CONNECTED;
			throw new RifidiConnectionException("No ReaderPlugin service.");
		}
		
		if (messageService == null) {
			status = ReaderSessionStatus.CONNECTED;
			throw new RifidiConnectionException("No Message service.");
		}
			
		if (connectionManager == null)
			connectionManager = readerPluginService.getReaderPlugin(
					readerInfo.getClass()).getConnectionManager(readerInfo);

		insureConnection();
		
		/* look for and execute the command */
		for (Class<? extends Command> commandClass : readerPluginService
				.getReaderPlugin(readerInfo.getClass()).getAvailableCommands()) {
			System.out.println(commandClass.getName());

			CommandDesc commandDesc = commandClass
					.getAnnotation(CommandDesc.class);
			if (commandDesc == null) {
				status = ReaderSessionStatus.CONNECTED;
				throw new RifidiCommandInterruptedException("Command not found.");
			} else {
				if (commandDesc.name().equals(command)) {
					try {
						Constructor<? extends Command> constructor = commandClass
								.getConstructor();

						commandInstance = (Command) constructor.newInstance();

						if (messageQueue == null) 
							messageQueue = messageService
								.createMessageQueue();

						try {
							commandInstance.start(connection, messageQueue);
						} catch (IOException e) {
							insureConnection();
							// try one more time...
							try {
								commandInstance.start(connection, messageQueue);
							} catch (IOException e1) {
								status = ReaderSessionStatus.DISCONNECTED;
								throw new RifidiConnectionException(
										"Error after exhausting "
												+ connectionManager
														.getMaxNumConnectionsAttemps()
												+ "attempts. Probably have a unreliable connection",
										e1);
							}
						} catch (ClassCastException e) {
							status = ReaderSessionStatus.CONNECTED;
							throw new RifidiCommandInterruptedException(
									"Command interrupted due to unexpected ClassCastExecption. "
											+ "Probibly tried to execute a command that did not belong to this reader, "
											+ "or something else caused it.", e);
						} catch (RuntimeException e) {
							status = ReaderSessionStatus.CONNECTED;
							throw new RifidiCommandInterruptedException(
									"Command interrupted due to unexpected RuntimeException. "
											+ "This may be caused by a bug in the command implemenation itself or, "
											+ "or the protocol implemenation of the reader.",
									e);
						}

					} catch (SecurityException e) {
						status = ReaderSessionStatus.CONNECTED;
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (NoSuchMethodException e) {
						status = ReaderSessionStatus.CONNECTED;
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (IllegalArgumentException e) {
						status = ReaderSessionStatus.CONNECTED;
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (InstantiationException e) {
						status = ReaderSessionStatus.CONNECTED;
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (IllegalAccessException e) {
						status = ReaderSessionStatus.CONNECTED;
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} catch (InvocationTargetException e) {
						status = ReaderSessionStatus.CONNECTED;
						throw new RifidiCommandInterruptedException(
								"Command not found.", e);
					} 
				}
			}
		}

		status = ReaderSessionStatus.CONNECTED;

	}

	private void insureConnection() throws RifidiConnectionException {
		connection = connectionService.createConnection(
				connectionManager, readerInfo, this);
		
		


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
	public void setReaderobjPluginService(ReaderPluginService readerPluginService) {
		this.readerPluginService = readerPluginService;
	}

	@Inject
	public void setMessageService(MessageService messageService) {
		System.out.println("MessageService injected");
		this.messageService = messageService;
	}

	/*=============================================================*/
	/*  ConnectionEventListener Below                              */
	/*=============================================================*/
	@Override
	public void connected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
	}

	@Override
	public void connectionExceptionEvent(Exception exception) {
		// TODO Auto-generated method stub
		// fires whenever there connections momentarily fails.
	}

	@Override
	public void disconnectedOnError() {
		// TODO Auto-generated method stub
		// fires when we gave up trying to maintain the connection
	}
	
	/*==============================================================*/
	@Override
	public void finalize(){
		
	}


}
