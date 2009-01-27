package org.rifidi.edge.core.api.exceptions;

/**
 * Exception thrown if the was a unexpected event on the MessageQueue
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class RifidiMessageQueueException extends RifidiException {

	/**
	 * Serial Version ID
	 */
	private static final long serialVersionUID = 6254147128620559396L;

	/**
	 * Create a new RifidiMessageQueueException
	 */
	public RifidiMessageQueueException() {
		super();
	}

	/**
	 * Create a new RifidiMessageQueueException
	 * 
	 * @param arg0
	 *            Message
	 * @param arg1
	 *            previous Exception
	 */
	public RifidiMessageQueueException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * Create a new RifidiMessageQueueException
	 * 
	 * @param arg0
	 *            Message
	 */
	public RifidiMessageQueueException(String arg0) {
		super(arg0);
	}

	/**
	 * Create a new RifidiMessageQueueException
	 * 
	 * @param arg0
	 *            previous Exception
	 */
	public RifidiMessageQueueException(Throwable arg0) {
		super(arg0);
	}

}
