package org.rifidi.edge.core.communication.buffer;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.ICommunicationConnection;


/**
 * @author jerry
 *
 */
public class CommunicationConnection implements ICommunicationConnection, Thread.UncaughtExceptionHandler {
	private static final Log logger = LogFactory.getLog(CommunicationConnection.class);	
	

	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;

	Exception exception;

	public CommunicationConnection(LinkedBlockingQueue<Object> readQueue,
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
	public Object recieve() throws IOException {
		if (exception != null) {
			if (exception instanceof IOException)
				throw (IOException) exception;

			if (exception instanceof RuntimeException)
				throw (RuntimeException) exception;

			throw new RuntimeException(
					"Unexpected non-RuntimeException in read or write thread",
					exception);
		}

		try {
			logger.debug("Trying to recieve a message");
			return readQueue.take();

		} catch (InterruptedException e) {
			if (exception != null){
				if (exception instanceof IOException )
					throw (IOException) exception;
				
				if (exception instanceof RuntimeException )
					throw (RuntimeException) exception;
				
				throw new RuntimeException("Unexpected non-RuntimeException in read or write thread", exception);
			}

		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.communication.ICommunicationConnection#recieveNonBlocking()
	 */
	@Override
	public Object recieveNonBlocking() throws IOException {
		if (exception != null) {
			if (exception instanceof IOException)
				throw (IOException) exception;

			if (exception instanceof RuntimeException)
				throw (RuntimeException) exception;

			throw new RuntimeException(
					"Unexpected non-RuntimeException in read or write thread",
					exception);
		}

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
	public Object recieveTimeOut(long mills) throws IOException {
		if (exception != null) {
			if (exception instanceof IOException)
				throw (IOException) exception;

			if (exception instanceof RuntimeException)
				throw (RuntimeException) exception;

			throw new RuntimeException(
					"Unexpected non-RuntimeException in read or write thread",
					exception);
		}

		Object retVal = null;
		try {
			readQueue.poll(mills, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {

			if (exception != null){
				if (exception instanceof IOException )
					throw (IOException) exception;
				
				if (exception instanceof RuntimeException )
					throw (RuntimeException) exception;
				
				throw new RuntimeException("Unexpected non-RuntimeException in read or write thread", exception);
			}

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
		if (exception != null) {
			if (exception instanceof IOException)
				throw (IOException) exception;

			if (exception instanceof RuntimeException)
				throw (RuntimeException) exception;

			throw new RuntimeException(
					"Unexpected non-RuntimeException in read or write thread.",
					exception);
		}
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
			
		} else {
			t.getThreadGroup().uncaughtException(t, e);
		}
	}
}
