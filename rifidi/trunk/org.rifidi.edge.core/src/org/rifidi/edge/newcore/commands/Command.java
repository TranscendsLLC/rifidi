package org.rifidi.edge.newcore.commands;

import java.util.concurrent.Callable;

import org.rifidi.edge.core.api.readerplugin.messageQueue.EventQueue;
import org.rifidi.edge.newcore.readers.Reader;

/**
 * This is the command Interface defining a common base over all Commands
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class Command implements
		Callable<CommandState> {

	private Reader reader;
	private EventQueue eventQueue;


	/**
	 * @param reader the reader to set
	 */
	public void setReader(Reader reader) {
		this.reader = reader;
	}

	/**
	 * @param eventQueue the messageQueue to set
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

	/**
	 * Stop the execution of this command
	 */
	public abstract void stop();

}