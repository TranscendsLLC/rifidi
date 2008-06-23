package org.rifidi.edge.core.communication.buffer;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.ICommunicationConnection;


public class CommunicationConnection implements ICommunicationConnection, Thread.UncaughtExceptionHandler {
	private static final Log logger = LogFactory.getLog(CommunicationConnection.class);	
	
	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	
	Exception exception;
	
	public CommunicationConnection(LinkedBlockingQueue<Object> readQueue, LinkedBlockingQueue<Object> writeQueue){
		this.readQueue = readQueue;
		this.writeQueue = writeQueue;
	}

	public Object recieve() throws Exception {
		try {
			logger.debug("Trying to recieve a message");
			return readQueue.take();


		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (exception != null){
			throw exception;
		}
		return null;
	}
	
	/**
	 * 
	 * @param msg
	 */
	public void send(Object msg) throws Exception {
		logger.debug("Trying to send a message: " + msg );
		writeQueue.add(msg);
		if (exception != null){
			throw exception;
		}
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		// TODO Auto-generated method stub
		if (e instanceof Exception) {
			logger.debug("Saw exception " + e.getClass().getName());
			exception = (Exception) e;
		} else {
			t.getThreadGroup().uncaughtException(t, e);
		}
	}

}
