package org.rifidi.edge.readerplugin.dummy.commands;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;

@CommandDesc(name = "StreamTagsTest")
public class StreamTagsTest implements Command {

	boolean running = true;

	private Log logger = LogFactory.getLog(StreamTagsTest.class);

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue) {
		while (running) {
			try {
				connection.sendMessage("GET_SOMETHING\n");
				logger.debug((String) connection.recieveMessage());
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
				}
			} catch (IOException e) {
				return CommandReturnStatus.UNSUCCESSFUL;
			}

		}

		return CommandReturnStatus.SUCCESSFUL;
	}

	@Override
	public void stop() {
		running = false;

	}

}
