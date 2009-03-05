/**
 * 
 */
package org.rifidi.edge.core.readers;

import java.util.Queue;

import javax.jms.Destination;

import org.springframework.jms.core.JmsTemplate;

/**
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
	/** Queue for incoming messages. */
	protected Queue<ByteMessage> input;
	/** Queue for outgoing messages. */
	protected Queue<ByteMessage> output;

	/**
	 * @param input
	 *            the input to set
	 */
	public void setInput(Queue<ByteMessage> input) {
		this.input = input;
	}

	/**
	 * @param output
	 *            the output to set
	 */
	public void setOutput(Queue<ByteMessage> output) {
		this.output = output;
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
