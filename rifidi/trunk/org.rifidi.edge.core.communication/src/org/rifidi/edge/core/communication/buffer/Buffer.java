/* 
 * Buffer.java
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.core.communication.buffer;

import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.communication.threads.ReadRunnable;
import org.rifidi.edge.core.communication.threads.WriteRunnable;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class Buffer {
	
	private Thread readThread;
	private Thread writeThread;
	
	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	
	public Buffer(Protocol protocol) {
		
		readQueue = new LinkedBlockingQueue<Object>();
		writeQueue = new LinkedBlockingQueue<Object>();
		
		readThread = new Thread(new ReadRunnable(protocol, readQueue));
		writeThread = new Thread(new WriteRunnable(protocol, writeQueue));
	}
	
	public void startBuffer()
	{
		readThread.start();
		writeThread.start();
	}
	
	/**
	 * 
	 * @return
	 */
	public Object recieve() {
		try {
			return readQueue.take();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * @param msg
	 */
	public void send(Object msg) {
		writeQueue.add(msg);
	}
}
