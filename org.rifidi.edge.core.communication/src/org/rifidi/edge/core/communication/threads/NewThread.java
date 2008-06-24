package org.rifidi.edge.core.communication.threads;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.buffer.CommunicationConnection;

public abstract class NewThread implements Runnable {
	private static final Log logger = LogFactory.getLog(CommunicationConnection.class);	
	
	Thread thread;
	
	protected boolean running = false;
	
	protected NewThread(String threadName){
		thread = new Thread(this, threadName);
		
	}
	
	public void start(){
		logger.debug("Starting");
		running = true;
		thread.start();
		
	}
	
	public void stop(){
		logger.debug("Stoping");
		running = false;
		try {
			thread.join(1000);
			if(thread.isAlive())
			{
				thread.interrupt();
			}
		} catch (InterruptedException e) {
	
		}
	
	}
	
	public void setUncaughtExceptionHandler(Thread.UncaughtExceptionHandler eh){
		thread.setUncaughtExceptionHandler(eh);
	}
}
