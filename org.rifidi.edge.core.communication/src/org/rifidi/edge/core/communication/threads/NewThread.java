package org.rifidi.edge.core.communication.threads;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.buffer.CommunicationBufferImpl;

/**
 * @author jerry
 *
 */
public abstract class NewThread implements Runnable {
	private static final Log logger = LogFactory.getLog(CommunicationBufferImpl.class);	
	
	Thread thread;
	
	protected boolean running = false;
	
	protected NewThread(String threadName){
		thread = new Thread(this, threadName);
		
	}
	
	/**
	 * 
	 */
	public void start(){
		logger.debug("Starting " + thread.getName());
		running = true;
		thread.start();
		
	}
	
	/**
	 * 
	 */
	public void stop(){
		logger.debug("Stoping " + thread.getName());
		running = false;
		try {
			thread.join(1000);
			if(thread.isAlive())
			{
				synchronized (thread) {
					thread.interrupt();
				}
			}
		} catch (InterruptedException e) {
	
		}
	
	}
	
	/**
	 * @param eh
	 */
	public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh){
		thread.setUncaughtExceptionHandler(eh);
	}
}
