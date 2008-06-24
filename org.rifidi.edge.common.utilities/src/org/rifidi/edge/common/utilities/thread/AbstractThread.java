package org.rifidi.edge.common.utilities.thread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author jerry
 *
 */
public abstract class AbstractThread implements Runnable {
	private static final Log logger = LogFactory.getLog(AbstractThread.class);	
	
	Thread thread;
	
	protected boolean running = false;
	
	protected AbstractThread(String threadName){
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
