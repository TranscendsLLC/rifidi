package org.rifidi.edge.core.connection.jms;

import org.rifidi.edge.common.utilities.thread.AbstractThread;

public class JMSMessagePoolThread extends AbstractThread {

	boolean isStalled = false;
	
	public JMSMessagePoolThread(String threadName) {
		super(threadName);
		// TODO Auto-generated constructor stub
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
