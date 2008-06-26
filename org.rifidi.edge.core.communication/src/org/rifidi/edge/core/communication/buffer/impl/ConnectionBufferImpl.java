package org.rifidi.edge.core.communication.buffer.impl;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.exception.readerConnection.RifidiIllegalOperationException;

public class ConnectionBufferImpl implements ConnectionBuffer,
		Thread.UncaughtExceptionHandler {
	
	private Log logger = LogFactory.getLog(ConnectionBufferImpl.class);

	private BlockingQueue<Object> readQueue;
	private BlockingQueue<Object> writeQueue;

	private IOException exception = null;

	private Set<Thread> currentThreads;

	public ConnectionBufferImpl(BlockingQueue<Object> readQueue,
			BlockingQueue<Object> writeQueue) {
		this.readQueue = readQueue;
		this.writeQueue = writeQueue;

		this.currentThreads = new HashSet<Thread>();
	}

	@Override
	public Object recieve() throws RifidiIllegalOperationException, IOException {
		checkForException();
		synchronized (currentThreads) {
			currentThreads.add(Thread.currentThread());
		}	
		try {
			return readQueue.take();
		} catch (InterruptedException e) {
			checkForException();
		} finally {
			synchronized (currentThreads) {
				currentThreads.remove(Thread.currentThread());
			}	
		}
		return null;
	}

	@Override
	public void send(Object message) throws RifidiIllegalOperationException,
			IOException {
		writeQueue.offer(message);
	}

	@Override
	public Object sendAndRecieve(Object message)
			throws RifidiIllegalOperationException, IOException {
		throw new RifidiIllegalOperationException(
				"readAndRecieve() is not allowed in Asynchronous mode");
	}

	private void checkForException() throws IOException {
		if (exception != null)
			throw exception;
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (e instanceof IOException) {
			logger.debug("There was an Exception in "
					+ "the underlying communication layer: "
					+ e.getClass().getName());
			exception = (IOException) e;
		} else {
			t.getThreadGroup().uncaughtException(t, e);
		}

		// Cause all waiting threads to interrupt to get notification about that
		// exception we just catch
		synchronized (currentThreads) {
			for (Thread interruptThread : currentThreads) {
				interruptThread.interrupt();
			}
		}
	

	}
}
