/**
 * 
 */
package org.rifidi.edge.core.commands;

import javax.jms.Destination;

import org.rifidi.edge.core.readers.ReaderSession;
import org.springframework.jms.core.JmsTemplate;

/**
 * An Abstract base class that must be extended by all Reader Commands.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class Command implements Runnable {
	/** Spring JMS template for easy sending of JMS-Messages. */
	protected JmsTemplate template;
	/** Destination for JMS-Messages. */
	protected Destination destination;
	/** The session this command is executed in. */
	protected ReaderSession readerSession;
	/** The ID of the commandConfiguraiton (the RifidiService) */
	private String commandID;

	/**
	 * Constructor
	 * 
	 * @param commandID
	 *            The ID of the commandconfiguration that produced this
	 *            command(The RifidiService)
	 */
	public Command(String commandID) {
		this.commandID = commandID;
	}

	/**
	 * @return the commandID
	 */
	public String getCommandID() {
		return this.commandID;
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
	 * @param readerSession
	 *            the readerSession to set
	 */
	public void setReaderSession(ReaderSession readerSession) {
		this.readerSession = readerSession;
	}

}
