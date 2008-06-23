package org.rifidi.edge.core.communication.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Protocol;


public class ReadRunnable implements Runnable {
	private static final Log logger = LogFactory.getLog(ReadRunnable.class);

	private Protocol protocol;
	private LinkedBlockingQueue<Object> readQueue;

	boolean running = true;
	private InputStream inputStream;
	
	
	public ReadRunnable(Protocol protocol, LinkedBlockingQueue<Object> readQueue, InputStream inputStream) {
		this.protocol = protocol;
		this.readQueue = readQueue;
		this.inputStream = inputStream;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		logger.debug("Starting Write thread");
		try {
			while(running){
				byte[] input = readFromSocket(inputStream);
				List<Object> msg =  protocol.toObject(input);
				logger.debug(msg);
				readQueue.add(msg);
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
		}
		
		return buffer.toByteArray();
	}
}
