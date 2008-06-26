package org.rifidi.edge.core.communication.threads;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.BlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.thread.AbstractThread;
import org.rifidi.edge.core.communication.protocol.Protocol;

public class ReadWriteThread extends AbstractThread {

	private Log logger = LogFactory.getLog(ReadWriteThread.class);

	private BlockingQueue<Object> readQueue;
	private BlockingQueue<Object> writeQueue;

	private Protocol protocol;
	private InputStream in;
	private OutputStream out;

	public ReadWriteThread(String threadName, Protocol protocol,
			InputStream in, OutputStream out, BlockingQueue<Object> readQueue,
			BlockingQueue<Object> writeQueue) {
		super(threadName);
		this.readQueue = readQueue;
		this.writeQueue = writeQueue;
		this.in = in;
		this.out = out;
		this.protocol = protocol;
	}

	@Override
	public void run() {
		logger.debug("Starting ReadAndWrite Thread");
		try {
			while (running) {
				logger.debug("in the while loop, taking from write queue");
				sendData(writeQueue.take());
				logger.debug("before the read statement");
				readQueue.add(readData());
				logger.debug("after the read statement");
			}
		} catch (InterruptedException e) {
			running = false;
		} catch (IOException e) {
			running = false;
			Thread.currentThread().getUncaughtExceptionHandler()
					.uncaughtException(Thread.currentThread(), e);
		}

	}

	private void sendData(Object message) throws InterruptedException, IOException {
		byte[] bytes = protocol.fromObject(message);
		out.write(bytes);
		out.flush();
	}

	private Object readData() throws IOException {
		byte[] input = readFromSocket(in);
		// TODO Think about what to do if the byte array is empty
		List<Object> msg = protocol.toObject(input);
		if (msg.size() > 1) {
			throw new IOException("To many message from reader");
		}
		if (msg.isEmpty())
			throw new IOException("No repsonse recieved");
		return msg.get(0);
	}

	private byte[] readFromSocket(InputStream inputStream) throws IOException {
		int input;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		
		while ((input = inputStream.read()) != -1) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
			buffer.write(input);

			// TODO Not sure this is going to work for every reader
			if (inputStream.available() == 0)
				break;
		}
		return buffer.toByteArray();
	}

}
