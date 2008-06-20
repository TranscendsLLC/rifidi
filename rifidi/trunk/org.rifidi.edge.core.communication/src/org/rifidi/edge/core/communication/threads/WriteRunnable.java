package org.rifidi.edge.core.communication.threads;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import org.rifidi.edge.core.communication.buffer.Protocol;

public class WriteRunnable implements Runnable{

	
	
	
	private Protocol protocol;
	private LinkedBlockingQueue<Object> writeQueue;
	private OutputStream outputStream;
	
	private boolean running = false;

	public WriteRunnable(Protocol protocol, LinkedBlockingQueue<Object> writeQueue, OutputStream outputStream) {
		// TODO Auto-generated constructor stub
		
		this.protocol = protocol;
		this.writeQueue = writeQueue;
		this.outputStream = outputStream;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			while(running) {
			
				Object object = writeQueue.take();
				
				byte[] bytes = protocol.fromObject(object);
				
				outputStream.write(bytes);
			}
		} catch (InterruptedException e) {
			// TODO Think about what to do with this exception
			running = false;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Think about what to do with this exception
			running = false;
			e.printStackTrace();
		}
	}

}
