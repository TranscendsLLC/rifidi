/**
 * 
 */
package org.rifidi.edge.newcore.internal.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.rifidi.edge.core.api.readerplugin.messageQueue.MessageQueue;
import org.rifidi.edge.newcore.Command;
import org.rifidi.edge.newcore.CommandState;
import org.rifidi.edge.newcore.Reader;
import org.rifidi.edge.newcore.ReaderConfiguration;
import org.rifidi.edge.newcore.exceptions.NoReaderAvailableException;
import org.rifidi.edge.newcore.internal.ReaderSession;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReaderSessionImpl implements ReaderSession {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ReaderSessionImpl.class);
	/** Command the session is executing. */
	private Command command;
	/** Factory used for aquiring readers. */
	private ReaderConfiguration factory;
	/** True if the command is currently executing on a reader. */
	private boolean running;
	/** True if the destroy method was hit. */
	private boolean dying;
	/** Service registration for the service. */
	private ServiceRegistration registration;
	/** Message queue for outgoing messages. */
	private MessageQueue messageQueue;

	/**
	 * Constructor.
	 */
	public ReaderSessionImpl() {
		running = false;
		dying = false;
		logger.debug("Reader session created.");
	}

	/**
	 * @param registration
	 *            the registration to set
	 */
	public void setRegistration(ServiceRegistration registration) {
		this.registration = registration;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.internal.ReaderSession#destroy()
	 */
	@Override
	public void destroy() {
		dying = true;
		stop();
		registration.unregister();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.internal.ReaderSession#getCommand()
	 */
	@Override
	public Command getCommand() {
		return command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.internal.ReaderSession#getReaderFactory()
	 */
	@Override
	public ReaderConfiguration getReaderFactory() {
		return factory;
	}

	/*
	 * (non-Javadoc) logger.warn("Unable to aquire reader: " + e);
	 * 
	 * @see org.rifidi.edge.newcore.internal.ReaderSession#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return running;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.internal.ReaderSession#setCommmand(org.rifidi
	 * .edge.core.api.readerplugin.Command)
	 */
	@Override
	public void setCommmand(Command command) {
		assert (!running);
		assert (command != null);
		logger.debug("Setting command: " + command);
		this.command = command;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.internal.ReaderSession#setReaderFactory(org.rifidi
	 * .edge.core.api.readerplugin.ReaderFactory)
	 */
	@Override
	public void setReaderFactory(ReaderConfiguration readerFactory) {
		assert (!running);
		assert (readerFactory != null);
		logger.debug("Setting reader factory: " + readerFactory);
		this.factory = readerFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.newcore.internal.ReaderSession#setMessageQueue(org.rifidi
	 * .edge.core.api.readerplugin.messageQueue.MessageQueue)
	 */
	@Override
	public void setMessageQueue(MessageQueue messageQueue) {
		assert (!running);
		assert (messageQueue != null);
		logger.debug("Setting message queue: " + messageQueue);
		this.messageQueue = messageQueue;
	}

	private boolean canStart() {
		return command != null && factory != null && messageQueue != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.concurrent.Callable#call()
	 */
	@Override
	public Boolean call() throws Exception {
		if (!canStart())
			return false;
		logger.debug("Starting with: " + command + " " + factory);
		try {
			Reader reader = factory.aquireReader();
			// command.setReader(reader);
			// // TODO: get a message queue in here
			// command.setMessageQueue(null);
			Future<CommandState> future = reader.execute(command);
			running = true;
			CommandState state = future.get();
			switch (state) {
			case DONE:
				break;
			case KILLED:
				break;
			case FAILED:
				break;
			case LOSTCONNECTION:
				return call();
			}
		} catch (NoReaderAvailableException e) {
			logger.warn("Unable to aquire reader: " + e);
		} catch (InterruptedException e) {
			logger.debug("Terminated: " + e);
			Thread.currentThread().interrupt();
		} catch (ExecutionException e) {
			logger.error("Failed executing: " + e);
		}
		running = false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.newcore.internal.ReaderSession#stop()
	 */
	@Override
	public void stop() {
		if (!running)
			return;
		logger.debug("Stopping command: " + command);
		command.stop();
	}

}
