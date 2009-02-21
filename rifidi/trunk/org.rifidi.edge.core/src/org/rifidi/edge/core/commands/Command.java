package org.rifidi.edge.core.commands;

import java.util.concurrent.Callable;

import javax.jms.Destination;

import org.rifidi.configuration.RifidiService;
import org.rifidi.edge.core.readers.Reader;
import org.springframework.jms.core.JmsTemplate;

/**
 * This is the command Interface defining a common base over all Commands
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class Command implements Callable<CommandState>, RifidiService {
	/** Reader used by the command. */
	private Reader reader;
	/** Spring JMS template for easy sending of JMS-Messages. */
	protected JmsTemplate template;
	/** Destination for JMS-Messages. */
	protected Destination destination;
	/** ID of the command. */
	private String id;

	/**
	 * @param reader
	 *            the reader to set
	 */
	public void setReader(Reader reader) {
		this.reader = reader;
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