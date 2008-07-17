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
package org.rifidi.edge.core.communication.threads;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract Thread to simplify start and stop of a new created Thread
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public abstract class AbstractThread implements Runnable {

	private static final Log logger = LogFactory.getLog(AbstractThread.class);

	private Thread thread;

	protected boolean running = false;

	protected boolean ignoreExceptions;

	/**
	 * Create a new Thread with the name
	 * 
	 * @param threadName
	 *            name of the Thread
	 */
	protected AbstractThread(String threadName) {
		thread = new Thread(this, threadName);
	}

	/**
	 * Start the thread
	 */
	public boolean start() {
		// logger.debug("Starting " + thread.getName());
		running = true;
		thread.start();
		return true;
	}

	/**
	 * Stop the thread
	 */
	public void stop() {
		logger.debug("Try stopping " + thread.getName());
		running = false;
		try {
			thread.join(1000);
			if (thread.isAlive()) {
				synchronized (thread) {
					thread.interrupt();
				}
			}
		} catch (InterruptedException e) {
			// Ignore
		}

	}

	/**
	 * Test if thread is still alive
	 * 
	 * @return true if thread is still alive
	 */
	public boolean isAlive() {
		return thread.isAlive();
	}

	/**
	 * Interrupt the thread
	 */
	public void interrupt() {
		thread.interrupt();
	}

	/**
	 * Test if the tread was interrupted
	 * 
	 * @return true if thread was interrupted
	 */
	public boolean isInterrupted() {
		return thread.isInterrupted();
	}

	/**
	 * Ignore Exceptions
	 * 
	 * @param ignore
	 *            true if the exceptions should be ignored
	 */
	public void ignoreExceptions(boolean ignore) {
		this.ignoreExceptions = ignore;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return thread.getName();
	}

	/**
	 * Get the name of the thread
	 * 
	 * @return name of the thread
	 */
	public String getName() {
		return thread.getName();
	}
}
