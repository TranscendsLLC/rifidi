package org.rifidi.edge.core.communication.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.communication.buffer.Protocol;

public class ReadRunnable implements Runnable{


	private Protocol protocol;
	private LinkedBlockingQueue<Object> readQueue;

	boolean running = false;
	private InputStream inputStream;
	
	
	public ReadRunnable(Protocol protocol, LinkedBlockingQueue<Object> readQueue, InputStream inputStream) {
		this.protocol = protocol;
		this.readQueue = readQueue;
		this.inputStream = inputStream;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(running){
				byte[] input = readFromSocket(inputStream);
				List<Object> msg =  protocol.toObject(input);
				readQueue.add(msg);
			}
		} catch (IOException e) {
			running = false;
			e.printStackTrace();
			
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
