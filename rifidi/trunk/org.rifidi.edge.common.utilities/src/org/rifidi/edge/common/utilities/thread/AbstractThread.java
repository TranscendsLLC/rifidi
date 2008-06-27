/*
 *  AbstractThread.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.common.utilities.thread;

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
		logger.debug("Stopping " + thread.getName());
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
