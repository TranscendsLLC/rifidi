package org.rifidi.edge.adminclient.testreaderthread;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public abstract class AbstractThread implements Runnable {
	private static final Log logger = LogFactory.getLog(AbstractThread.class);	
	
	private Thread thread;
	
	protected boolean running = false;
	
	protected AbstractThread(String threadName){
		thread = new Thread(this, threadName);
		
	}
	
	/**
	 * 
	 */
	public boolean start(){
		logger.debug("Starting " + thread.getName());
		running = true;
		thread.start();
		return true;
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
	

	public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh){
		thread.setUncaughtExceptionHandler(eh);	
	}
	
	public boolean isAlive(){
		return thread.isAlive();
		
	}
	
	public void interrupt(){
		thread.interrupt();
	}
	
	public boolean isInterrupted(){
		return thread.isInterrupted();
	}
}
