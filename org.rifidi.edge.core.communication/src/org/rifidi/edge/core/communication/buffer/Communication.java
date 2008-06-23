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

import org.rifidi.edge.core.communication.ICommunicationConnection;
import org.rifidi.edge.core.communication.Protocol;
import org.rifidi.edge.core.communication.threads.ReadRunnable;
import org.rifidi.edge.core.communication.threads.WriteRunnable;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class Communication {
	
	private Thread readThread;
	private Thread writeThread;
	
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
		
		readThread = new Thread(new ReadRunnable(protocol, readQueue, socket.getInputStream()));

		writeThread = new Thread(new WriteRunnable(protocol, writeQueue, socket.getOutputStream()));

		CommunicationConnection communicationConnection = new CommunicationConnection(readQueue, readQueue);
		
		readThread.start();
		writeThread.start();
		
		readThread.setUncaughtExceptionHandler(communicationConnection);
		writeThread.setUncaughtExceptionHandler(communicationConnection);

		return communicationConnection;
	}

	public Socket getSocket(){
		return socket;
	}
}
