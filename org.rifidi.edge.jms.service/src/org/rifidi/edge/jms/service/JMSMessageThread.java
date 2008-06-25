package org.rifidi.edge.jms.service;

import org.rifidi.edge.common.utilities.thread.AbstractThread;

public class JMSMessageThread extends AbstractThread {

	boolean isStalled = false;
	
	public JMSMessageThread(String threadName) {
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
