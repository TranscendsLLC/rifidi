/**
 * 
 */
package org.rifidi.edge.newcore.impl;

import java.io.IOException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.rifidi.edge.newcore.Command;
import org.rifidi.edge.newcore.CommandState;
import org.rifidi.edge.newcore.Reader;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class AbstractReader implements Reader {
	/** Queue for reading messages. */
	protected LinkedBlockingQueue<Object> readQueue = new LinkedBlockingQueue<Object>();
	/** Queue for writing messages. */
	protected LinkedBlockingQueue<Object> writeQueue = new LinkedBlockingQueue<Object>();
	/** Used to execute commands. */
	protected ThreadPoolExecutor executor;

	/**
	 * Constructor.
	 */
	public AbstractReader() {
		executor = new ThreadPoolExecutor(1, 1, 30, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.Reader#isMessageAvailable()
	 */
	@Override
	public boolean isMessageAvailable() throws IOException {
		return readQueue.peek() != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.Reader#receiveMessage()
	 */
	@Override
	public Object receiveMessage() throws IOException {
		try {
			return readQueue.take();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.Reader#receiveMessage(long)
	 */
	@Override
	public Object receiveMessage(long timeout) throws IOException {
		try {
			return readQueue.poll(timeout, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IOException();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.Reader#sendMessage(java.lang.Object)
	 */
	@Override
	public void sendMessage(Object o) throws IOException {
		writeQueue.add(o);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.Reader#execute(org.rifidi.edge.newcore.Command)
	 */
	@Override
	public Future<CommandState> execute(Command command) {
		return executor.submit(command);
	}

}
