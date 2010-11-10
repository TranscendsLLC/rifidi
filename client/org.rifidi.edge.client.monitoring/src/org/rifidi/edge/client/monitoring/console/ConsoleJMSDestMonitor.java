/**
 * 
 */
package org.rifidi.edge.client.monitoring.console;

import org.rifidi.edge.client.monitoring.AbstractJMSDestMonitor;

/**
 * This class monitors the JMS destination "org.rifidi.tracking". It simply
 * prints out any strings put on that destination to the console.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ConsoleJMSDestMonitor extends AbstractJMSDestMonitor {

	/** The console to print text to */
	private ConsoleManager console;
	private boolean pause = false;

	/**
	 * Constructor
	 */
	public ConsoleJMSDestMonitor() {
		this.console = new ConsoleManager();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.monitoring.AbstractJMSDestMonitor#handleMessage
	 * (java.lang.String)
	 */
	@Override
	protected void handleMessage(String message) {
		if (pause) {
			return;
		}
		console.writeLine(message);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.monitoring.JMSDestMonitor#getDestination()
	 */
	@Override
	protected String getDestination() {
		return "org.rifidi.monitor.events";
	}

	public void clearConsole() {
		console.clearConsole();
	}

	public void togglePause() {
		if (pause) {
			this.pause = false;
		} else {
			pause = true;
		}
	}

}
