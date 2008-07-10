package org.rifidi.edge.core.communication.impl;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiInvalidMessageFormat;
import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionExceptionListener;

public class ConnectionImpl implements Connection {

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	private Exception exception = null;

	private Object locker = new Object();
	
	// FIXME KYLE make me Thread safe
	private HashSet<Thread> blockedThreads = new HashSet<Thread>();

	private Set<ConnectionExceptionListener> listeners = new HashSet<ConnectionExceptionListener>();

	public ConnectionImpl(LinkedBlockingQueue<Object> readQueue,
			LinkedBlockingQueue<Object> writeQueue) {
		this.readQueue = readQueue;
		this.writeQueue = writeQueue;
	}

	@Override
	public Object recieveMessage() throws IOException {
		checkException();

		blockedThreads.add(Thread.currentThread());

		Object retVal = null;
		try {
			retVal = readQueue.take();
		} catch (InterruptedException e) {
			checkException();
		} finally {
			blockedThreads.remove(Thread.currentThread());
		}
		return retVal;
	}

	@Override
	public void sendMessage(Object o) throws IOException {
		checkException();
		writeQueue.offer(o);
	}

	@Override
	public boolean isMessageAvailable() throws IOException {
		checkException();
		return !readQueue.isEmpty();
	}

	public void setException(Exception e) {
		synchronized (locker) {
			this.exception = e;
			for (Thread thread : blockedThreads)
				thread.interrupt();
			for (ConnectionExceptionListener listener : listeners) {
				listener.connectionExceptionEvent(e);
			}
		}
	}

	private void checkException() throws IOException {
		synchronized (locker) {
			if (exception != null) {
				if (exception instanceof IOException){
					throw (IOException) exception;		
				} else if (exception instanceof RuntimeException) {
					throw (RuntimeException) exception;
				} else if (exception instanceof RifidiInvalidMessageFormat) {
					throw new IOException("Invalid Message Format!", exception);
				} else {
					throw new UndeclaredThrowableException(exception);
				}
			}
		}
	}

	public void addConnectionExceptionListener(
			ConnectionExceptionListener listener) {
		listeners.add(listener);
	}

	public void removeConnectionExceptionListener(
			ConnectionExceptionListener listener) {
		listeners.add(listener);
	}

	/*==============================================================*/
	@Override
	public void finalize(){
		listeners.clear();
	}
}
