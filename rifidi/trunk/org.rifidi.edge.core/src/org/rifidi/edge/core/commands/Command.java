package org.rifidi.edge.core.commands;

import java.util.concurrent.Callable;

import org.rifidi.configuration.RifidiService;
import org.rifidi.edge.core.api.readerplugin.messageQueue.EventQueue;
import org.rifidi.edge.core.readers.Reader;

/**
 * This is the command Interface defining a common base over all Commands
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class Command implements Callable<CommandState>, RifidiService {

	private Reader reader;
	private EventQueue eventQueue;
	private String id;

	/**
	 * @param reader
	 *            the reader to set
	 */
	public void setReader(Reader reader) {
		this.reader = reader;
	}

	/**
	 * @param eventQueue
	 *            the messageQueue to set
	 */
	public void setEventQueue(EventQueue eventQueue) {
		this.eventQueue = eventQueue;
	}

	/**
	 * Return the reader the command is running on.
	 * 
	 * @return
	 */
	public Reader getReader() {
		return reader;
	}

	/**
	 * Return the event queue for the command.
	 * 
	 * @return
	 */
	public EventQueue getEventQueue() {
		return eventQueue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.RifidiService#getID()
	 */
	@Override
	public String getID() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.configuration.RifidiService#setID(java.lang.String)
	 */
	@Override
	public void setID(String id) {
		this.id = id;
	}

	/**
	 * Stop the execution of this command
	 */
	public abstract void stop();

}