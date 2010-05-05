package org.rifidi.edge.client.monitoring.console;

import java.io.IOException;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

/**
 * This class manages an eclipse console. It exposes a method that clients can
 * use to print messages to the console.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ConsoleManager {

	/** The Console to print to */
	private MessageConsole console;
	/** The stream to use */
	private MessageConsoleStream stream;

	/**
	 * Constructor. Sets up the console
	 */
	public ConsoleManager() {
		this.console = new MessageConsole("Edge Server Events", null);
		this.stream = this.console.newMessageStream();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(
				new IConsole[] { this.console });
	}

	/**
	 * Write a line of text to the console
	 * 
	 * @param arg
	 *            The line to write.
	 */
	public void writeLine(String arg) {
		try {
			this.stream.write(arg + "\n");
		} catch (IOException ex) {

		}

	}
	
	public void clearConsole(){
		this.console.clearConsole();
	}
}
