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

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.ICommunicationConnection;
import org.rifidi.edge.core.communication.Protocol;
import org.rifidi.edge.core.communication.service.CommunicationServiceImpl;

import org.rifidi.edge.core.communication.threads.ReadThread;
import org.rifidi.edge.core.communication.threads.WriteThread;



public class Communication {
	private static final Log logger = LogFactory.getLog(CommunicationServiceImpl.class);
	
	private ReadThread readThread;
	private WriteThread writeThread;
	
	private LinkedBlockingQueue<Object> readQueue;
	private LinkedBlockingQueue<Object> writeQueue;
	
	private Socket socket;
	private Protocol protocol;
	
	public Communication(Socket socket, Protocol protocol ) {
		this.socket = socket;
		this.protocol = protocol;
		readQueue = new LinkedBlockingQueue<Object>();
		writeQueue = new LinkedBlockingQueue<Object>();
	}
	
	public ICommunicationConnection startCommunication() throws IOException
	{
		logger.debug("Trying to start communications");
		
		readThread = new ReadThread("Read Thread: " + socket.getInetAddress().toString() + ":" + socket.getPort(), protocol, readQueue, socket.getInputStream());

		writeThread = new WriteThread("Write Thread: " + socket.getInetAddress().toString() + ":" + socket.getPort(), protocol, writeQueue, socket.getOutputStream());


		
		CommunicationConnection communicationConnection = new CommunicationConnection(readQueue, readQueue);
		
		readThread.start();
		writeThread.start();
		readThread.setUncaughtExceptionHandler(communicationConnection);
		writeThread.setUncaughtExceptionHandler(communicationConnection);


		return communicationConnection;
	}
	
	public void stopCommunication(){
		readThread.stop();
		writeThread.stop();
	}

	public Socket getSocket(){
		return socket;
	}
}
