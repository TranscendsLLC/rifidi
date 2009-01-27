package org.rifidi.edge.core.communication.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.communication.Connection;

/**
 * This is the implementation of a Connection. It allows the Commands to send
 * and receive messages to the socket without being directly connected to the
 * socket. This implementation is using LinkedBlockingQueues to allow blocking
 * and avoid unnecessary polling. It will throw a Exception if the underlying
 * Communication fails.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ConnectionImpl implements Connection {

	private Log logger = LogFactory.getLog(ConnectionImpl.class);

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	private Exception exception = null;

	private Object locker = new Object();

	// FIXME KYLE make me Thread safe
	private HashSet<Thread> blockedThreads = new HashSet<Thread>();

	/**
	 * Create a new Connection Object
	 * 
	 * @param readQueue
	 *            the Queue to read from
	 * @param writeQueue
	 *            the Queue to write to
	 */
	public ConnectionImpl(LinkedBlockingQueue<Object> readQueue,
			LinkedBlockingQueue<Object> writeQueue) {
		this.readQueue = readQueue;
		this.writeQueue = writeQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.Connection#receiveMessage()
	 */
	@Override
	public Object receiveMessage() throws IOException {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.Connection#receiveMessage(long)
	 */
	@Override
	public Object receiveMessage(long timeout) throws IOException {
		checkException();

		blockedThreads.add(Thread.currentThread());

		Object retVal = null;
		try {
			retVal = readQueue.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			checkException();
		} finally {
			blockedThreads.remove(Thread.currentThread());
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.Connection#sendMessage(java.lang.Object)
	 */
	@Override
	public void sendMessage(Object o) throws IOException {
		checkException();
		writeQueue.offer(o);
	}

	// TODO: Threadsafe?
	@Override
	public void cleanQueues() {
		readQueue.clear();
		writeQueue.clear();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.Connection#isMessageAvailable()
	 */
	@Override
	public boolean isMessageAvailable() throws IOException {
		checkException();
		return !readQueue.isEmpty();
	}

	/**
	 * If a Exception on the underlying communication happened this method will
	 * allow the underlying communication to notify all Clients (or Objects
	 * using this Connection) that there was a problem.
	 * 
	 * @param e
	 *            the Exception occurred
	 */
	public void setException(Exception e) {
		logger.debug("Try to set Exception but need to pass lock");
		synchronized (locker) {
			logger.debug("Exception set in Connection");
			this.exception = e;
			readQueue.clear();
			writeQueue.clear();
			for (Thread thread : blockedThreads)
				thread.interrupt();
			locker.notifyAll();
		}
	}

	/**
	 * Check if there is a Problem with the underlying Communication. This needs
	 * to reset the Exception object to null so that when we use this object to
	 * reconnect, the exception is gone.
	 * 
	 * @throws IOException
	 *             if there was a problem on the underlying communication
	 */
	private void checkException() throws IOException {
		synchronized (locker) {
			Exception e = exception;
			exception = null;
			if (e != null) {
				logger.error("Exception found... Throwing it.", e);
				if (e instanceof IOException) {
					throw (IOException) e;
				} else {
					throw new IOException(e);
				}

			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	public void finalize() {
	}

}
