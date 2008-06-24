package org.rifidi.edge.core.communication.threads;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Protocol;

public class WriteThread extends NewThread {
	private static final Log logger = LogFactory.getLog(WriteThread.class);

	private Protocol protocol;
	private LinkedBlockingQueue<Object> writeQueue;
	private OutputStream outputStream;

	/**
	 * @param threadName
	 * @param protocol
	 * @param writeQueue
	 * @param outputStream
	 */
	public WriteThread(String threadName, Protocol protocol,
			LinkedBlockingQueue<Object> writeQueue, OutputStream outputStream) {
		super(threadName);

		this.protocol = protocol;
		this.writeQueue = writeQueue;
		this.outputStream = outputStream;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		logger.debug("Starting Write thread");
		try {
			while (running) {

				Object object = writeQueue.take();
				logger.debug(object);
				byte[] bytes = protocol.fromObject(object);

				outputStream.write(bytes);
				outputStream.flush();
			}
		} catch (InterruptedException e) {
			running = false;
			// e.printStackTrace();
		} catch (IOException e) {
			// TODO Think about what to do with this exception
			running = false;
			Thread.currentThread().getUncaughtExceptionHandler()
					.uncaughtException(Thread.currentThread(), e);
		}
	}

}
