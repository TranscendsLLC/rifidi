package org.rifidi.edge.core.readersession.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.communication.service.ConnectionService;
import org.rifidi.edge.core.exceptions.RifidiConnectionException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.messageQueue.service.MessageService;
import org.rifidi.edge.core.readerplugin.ReaderInfo;
import org.rifidi.edge.core.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readersession.ExecutionListener;
import org.rifidi.edge.core.readersession.ReaderSession;
import org.rifidi.edge.core.readersession.ReaderSessionStatus;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class ReaderSessionImpl implements ReaderSession {

	private ConnectionService connectionService = null;
	private MessageService messageService = null;

	private ReaderInfo readerInfo;
	private ReaderPlugin readerPlugin;

	private ReaderSessionStatus status = ReaderSessionStatus.DISCONNECTED;
	private Command commandInstance;

	private List<ExecutionListener> listeners = new ArrayList<ExecutionListener>();
	private ConnectionManager connectionManager;

	public ReaderSessionImpl(ReaderInfo readerInfo, ReaderPlugin readerPlugin) {
		this.readerInfo = readerInfo;
		this.readerPlugin = readerPlugin;
		ServiceRegistry.getInstance().service(this);
	}

	@Override
	public ReaderSessionStatus getStatus() {
		return status;
	}

	@Override
	public ReaderInfo getReaderInfo() {
		return readerInfo;
	}

	@Override
	public void executeCommand(String command) throws RifidiConnectionException {

		if (status != ReaderSessionStatus.CONNECTED) {
			initializeConnection();
		}

		for (Class<? extends Command> commandClass : readerPlugin
				.getAvailableCommands()) {
			System.out.println(commandClass.getName());

			CommandDesc commandDesc = commandClass
					.getAnnotation(CommandDesc.class);
			if (commandDesc != null) {
				System.out.println("Command found");
				if (commandDesc.name().equals(command)) {
					try {
						Constructor<? extends Command> constructor = commandClass
								.getConstructor();

						// TODO only need to happen once
						commandInstance = (Command) constructor.newInstance();

						MessageQueue messageQueue = messageService
								.createMessageQueue();

						status = ReaderSessionStatus.BUSY;

						fireExecutionStartedEvent(commandInstance);
						// commandInstance.start(connection, messageQueue);
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		fireExecutionStoppedEvent(commandInstance);

	}

	private void initializeConnection() {

		// this.wait();
	}

	@Override
	public void stopCommand() {
		fireExecutionStoppedEvent(commandInstance);
		commandInstance.stop();
	}

	@Override
	public void addExecutionListener(ExecutionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeExecutionListener(ExecutionListener listener) {
		listeners.remove(listener);
	}

	private void fireExecutionStartedEvent(Command event) {
		connectionManager.startKeepAlive();
		for (ExecutionListener listener : listeners) {
			listener.executionStarted(event);
		}
	}

	private void fireExecutionStoppedEvent(Command event) {
		connectionManager.stopKeepAlive();
		for (ExecutionListener listener : listeners) {
			listener.executionStopped(event);
		}
	}

	@Inject
	public void setConnectionService(ConnectionService connectionService) {
		System.out.println("ConnectionService injected");
		this.connectionService = connectionService;
	}

	@Inject
	public void setMessageService(MessageService messageService) {
		System.out.println("MessageService injected");
		this.messageService = messageService;
	}

}
