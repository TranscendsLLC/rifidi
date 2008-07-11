package org.rifidi.edge.core.communication.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.communication.Connection;

public class ConnectionImpl implements Connection {

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	private Exception exception = null;

	private Object locker = new Object();
	
	// FIXME KYLE make me Thread safe
	private HashSet<Thread> blockedThreads = new HashSet<Thread>();

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
			readQueue.clear();
			writeQueue.clear();
			for (Thread thread : blockedThreads)
				thread.interrupt();
			locker.notifyAll();
		}
	}

	private void checkException() throws IOException {
		synchronized (locker) {
			if (exception != null) {
				if (exception instanceof IOException){
					throw (IOException) exception;		
				} else {
					throw new IOException(exception);
				}
			}
		}
	}

	/*==============================================================*/
	@Override
	public void finalize(){
	}
}
