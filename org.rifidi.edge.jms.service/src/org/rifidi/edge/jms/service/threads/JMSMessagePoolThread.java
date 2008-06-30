/*
 * JMSMessagePollThread.java
 *
 *  Created:	Jun 27, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.jms.service.threads;

import org.rifidi.edge.common.utilities.thread.AbstractThread;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class JMSMessagePoolThread extends AbstractThread {

	boolean isStalled = false;
	
	public JMSMessagePoolThread(String threadName) {
		super(threadName);
	}
	
	
	
	@Override
	public void run() {
		while(running){
			
			isStalled = true;
		}
	}



	/**
	 * @return the isStalled
	 */
	public boolean isStalled() {
		return isStalled;
	}

}
