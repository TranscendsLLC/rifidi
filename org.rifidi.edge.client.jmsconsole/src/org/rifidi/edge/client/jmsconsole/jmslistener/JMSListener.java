package org.rifidi.edge.client.jmsconsole.jmslistener;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.rifidi.edge.client.connections.edgeserver.RemoteReader;
import org.rifidi.edge.client.connections.edgeserver.listeners.ReaderMessageListener;

public class JMSListener implements ReaderMessageListener {

	private Log logger = LogFactory.getLog(ReaderMessageListener.class);

	private RemoteReader reader;
	private MessageConsole messageConsole;
	private MessageConsoleStream stream = null;

	public JMSListener(RemoteReader reader) {
		logger.debug("JMSListener created");
		this.reader = reader;
		reader.addListener(this);
		getConsole();
		openConsole();
		stream = messageConsole.newMessageStream();
	}

	public void openConsole() {
		MessageConsole console = getConsole();
		if (console != null) {
			IConsoleManager manager = ConsolePlugin.getDefault()
					.getConsoleManager();
			IConsole[] existing = manager.getConsoles();
			boolean exists = false;
			for (int i = 0; i < existing.length; i++) {
				if (console == existing[i])
					exists = true;
			}
			if (!exists)
				manager.addConsoles(new IConsole[] { console });
			manager.showConsoleView(console);
		}
	}

	public MessageConsole getConsole() {
		if (messageConsole == null) {
			messageConsole = new MessageConsole("ReaderView", null, true);
			// Set the buffer of the Console
			messageConsole.setWaterMarks(5000, 100000);

		}
		return messageConsole;
	}

	public void closeConsole() {
		logger.debug("Well no yet done");
	}

	@Override
	protected void finalize() {
		logger.debug("JMSListener is gone");
	}

	@Override
	public void onMessage(Message message, RemoteReader reader) {
		if (!stream.isClosed()) {
			try {
				stream.write(((TextMessage) message).getText());
			} catch (IOException e) {
				// ignore
			} catch (JMSException e) {
				logger.error("Error", e);
			}
		} else {
			reader.removeListener(this);
			messageConsole = null;
			stream = null;
			reader = null;
		}
	}
}
