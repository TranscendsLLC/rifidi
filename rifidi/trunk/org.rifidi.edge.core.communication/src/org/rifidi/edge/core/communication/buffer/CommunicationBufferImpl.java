package org.rifidi.edge.core.communication.buffer;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jerry
 * 
 */
public class CommunicationBufferImpl implements CommunicationBuffer,
		Thread.UncaughtExceptionHandler {

	private static final Log logger = LogFactory
			.getLog(CommunicationBufferImpl.class);

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;

	private Exception exception;

	// Save the Threads to unblock them if there is an error on the Socket
	private Set<Thread> threads = new HashSet<Thread>();

	public CommunicationBufferImpl(LinkedBlockingQueue<Object> readQueue,
			LinkedBlockingQueue<Object> writeQueue) {
		this.readQueue = readQueue;
		this.writeQueue = writeQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.ICommunicationConnection#recieve()
	 */
	@Override
	public Object receive() throws IOException {
		checkState(exception);

		// Add the Thread to the list of Threads waiting
		threads.add(Thread.currentThread());
		try {
			return readQueue.take();
		} catch (InterruptedException e) {
			logger.debug("ERROR Happend ... ");
			checkState(exception);
		} finally {
			// Remove the Thread since it's not waiting anymore
			threads.remove(Thread.currentThread());
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.ICommunicationConnection#recieveNonBlocking()
	 */
	@Override
	public Object receiveNonBlocking() throws IOException {
		checkState(exception);

		logger.debug("Trying to recieve a message");
		Object retVal = readQueue.poll();

		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.ICommunicationConnection#recieveTimeOut(long)
	 */
	@Override
	public Object receiveTimeOut(long mills) throws IOException {
		checkState(exception);

		Object retVal = null;
		try {
			readQueue.poll(mills, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {

			checkState(exception);

		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.ICommunicationConnection#send(java.lang.Object)
	 */
	@Override
	public void send(Object msg) throws IOException {
		logger.debug("Trying to send a message: " + msg);
		writeQueue.add(msg);

		checkState(exception);
	}

	/**
	 * This method tests if an object is a IOException or a RuntimeException and
	 * throws it if it is true. It will also wrap any other Exception objects up
	 * in a RuntimeException object and throw the newly created exception.
	 * Otherwise the same object is returned.
	 * 
	 * @param object
	 *            Any Object
	 * @return The object sent to this method.
	 * @throws IOException
	 * @throws RuntimeException
	 */
	private Object checkState(Object object) throws IOException {
		logger.debug("Object is :" + exception);
		if (object != null) {
			/*
			 * These if statements are here so that we can 'throws IOException'
			 * in the method signature instead of 'throws Exception'

		logger.debug(object);
		
		synchronized (readQueue) {
			readQueue.notifyAll();
		}
		synchronized (writeQueue) {
			writeQueue.notifyAll();
		}

		
		if (object != null){
			/*These if statements are here so that we can 'throws IOException' in the method
			 * signature instead of 'throws Exception'
			 */
			if (object instanceof IOException)
				throw (IOException) object;

			if (object instanceof RuntimeException)
				throw (RuntimeException) object;

			if (object instanceof Exception)
				throw new RuntimeException(
						"Unexpected non-RuntimeException in read or write thread.",
						exception);

			return object;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread$UncaughtExceptionHandler#uncaughtException(java.lang.Thread,
	 *      java.lang.Throwable)
	 */
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (e instanceof Exception) {
			logger.debug("Saw exception " + e.getClass().getName());
			exception = (Exception) e;
			// TODO Put an interrupt object on the queue.... or exception
			// depending on what is decided.

		} else {
			t.getThreadGroup().uncaughtException(t, e);
		}

		logger.debug(exception);
		// Cause all waiting threads to interrupt to get notification about that
		// exception
		for (Thread interruptThread : threads) {
			interruptThread.interrupt();
		}
		logger.debug("Notify done!!!");
	}
}
