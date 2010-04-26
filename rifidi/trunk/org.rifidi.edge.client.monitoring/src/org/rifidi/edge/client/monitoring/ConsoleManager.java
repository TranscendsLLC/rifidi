package org.rifidi.edge.client.monitoring;

import java.io.IOException;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;

public class ConsoleManager {

	private MessageConsole console;
	private MessageConsoleStream stream;

	public ConsoleManager() {
		this.console = new MessageConsole("Edge Server Events", null);
		this.stream = this.console.newMessageStream();
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(
				new IConsole[] { this.console });
	}

	public void writeMessage(String text) {
		try {
			this.stream.write(text + "\n");
		} catch (IOException ex) {

		}

	}
}
