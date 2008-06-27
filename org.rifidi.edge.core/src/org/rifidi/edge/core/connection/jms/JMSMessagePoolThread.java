package org.rifidi.edge.core.connection.jms;

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
