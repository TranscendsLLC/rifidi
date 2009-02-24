/**
 * 
 */
package org.rifidi.edge.core.internal.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.rifidi.edge.core.commands.Command;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.CommandState;
import org.rifidi.edge.core.exceptions.NoReaderAvailableException;
import org.rifidi.edge.core.internal.ReaderSession;
import org.rifidi.edge.core.readers.Reader;
import org.rifidi.edge.core.readers.AbstractReaderConfiguration;
import org.springframework.jms.core.JmsTemplate;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReaderSessionImpl implements ReaderSession {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ReaderSessionImpl.class);
	/** Command the session is executing. */
	private AbstractCommandConfiguration<?> commandFactory;
	/** Factory used for aquiring readers. */
	private AbstractReaderConfiguration<?> factory;
	/** Currently aquired reader. */
	private Reader reader;
	/** True if the command is currently executing on a reader. */
	private AtomicBoolean running;
	/** Service registration for the service. */
	private ServiceRegistration registration;
	/** Currently executing command. */
	private Command command;
	/** Set to true if someone attempted to kill the command. */
	private AtomicBoolean dying;
	/** Spring JMS template for easy sending of JMS-Messages. */
	private JmsTemplate template;
	/** Destination for JMS-Messages. */
	private Destination destination;
	/** The ID for this reader session. */
	private String ID;

	/**
	 * Constructor.
	 */
	public ReaderSessionImpl() {
		running = new AtomicBoolean(false);
		dying = new AtomicBoolean(false);
		logger.debug("Reader session created.");
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
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
	 * @see org.rifidi.edge.core.internal.ReaderSession#getID()
	 */
	@Override
	public String getID() {
		return this.ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderSession#setID(java.lang.String)
	 */
	@Override
	public void setID(String id) {
		this.ID = id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderSession#destroy()
	 */
	@Override
	public void destroy() {

		registration.unregister();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderSession#getReaderFactory()
	 */
	@Override
	public AbstractReaderConfiguration<?> getReaderFactory() {
		return factory;
	}

	/*
	 * (non-Javadoc) logger.warn("Unable to aquire reader: " + e);
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderSession#isRunning()
	 */
	@Override
	public boolean isRunning() {
		return running.get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderSession#getCommandFactory()
	 */
	@Override
	public AbstractCommandConfiguration<?> getCommandFactory() {
		// TODO Auto-generated method stub
		return commandFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderSession#setCommmandFactory(org
	 * .rifidi.edge.newcore.commands.CommandFactory)
	 */
	@Override
	public void setCommmandFactory(
			AbstractCommandConfiguration<?> commandFactory) {
		assert (!running.get());
		assert (commandFactory != null);
		logger.debug("Setting command factory: " + commandFactory);
		this.commandFactory = commandFactory;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.internal.ReaderSession#setReaderFactory(org.rifidi
	 * .edge.core.api.readerplugin.ReaderFactory)
	 */
	@Override
	public void setReaderFactory(AbstractReaderConfiguration<?> readerFactory) {
		assert (!running.get());
		assert (readerFactory != null);
		logger.debug("Setting reader factory: " + readerFactory);
		this.factory = readerFactory;
	}

	private boolean canStart() {
		return commandFactory != null && factory != null;
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
		if (running.compareAndSet(false, true)) {
			try {
				logger
						.debug("Starting with: " + commandFactory + " "
								+ factory);
				try {
					reader = factory.aquireReader();
					command = commandFactory.getCommand();
					command.setReader(reader);
					command.setDestination(destination);
					command.setTemplate(template);
					Future<CommandState> future = reader.execute(command);
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
				return true;
			} finally {
				// reset so that the session can be started again
				running.compareAndSet(true, false);
				dying.compareAndSet(true, false);
				factory.releaseReader(reader);
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.internal.ReaderSession#stop()
	 */
	@Override
	public void stop() {
		if (dying.compareAndSet(false, true)) {
			logger.debug("Stopping command: " + command);
			command.stop();
		}
	}

}
