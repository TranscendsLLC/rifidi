package org.rifidi.edge.core.communication.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.Protocol;


public class ReadThread  extends NewThread {
	private static final Log logger = LogFactory.getLog(ReadThread.class);

	private Protocol protocol;
	private LinkedBlockingQueue<Object> readQueue;

	private InputStream inputStream;
	
	
	
	public ReadThread(String threadName, Protocol protocol, LinkedBlockingQueue<Object> readQueue, InputStream inputStream) {
		super(threadName);
		this.protocol = protocol;
		this.readQueue = readQueue;
		this.inputStream = inputStream;
		
		
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.debug("Starting Read thread");
		try {
			while(running){
				byte[] input = readFromSocket(inputStream);
				//TODO Think about what to do if the byte array is empty
				List<Object> msg =  protocol.toObject(input);
				for (Object o: msg){
					logger.debug(o);
					readQueue.add(o);
				}
			}
		} catch (IOException e) {
			running = false;
			Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), e);
		}
	}

	private byte[] readFromSocket(InputStream inputStream) throws IOException{
		int input;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		while((input = inputStream.read()) != -1){
			buffer.write(input);
			
			//TODO Not sure this is going to work for every reader
			if (inputStream.available() == 0)
				break;		
		}
		return buffer.toByteArray();
	}
	
}
