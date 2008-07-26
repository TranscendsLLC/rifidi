package org.rifidi.edge.readerplugin.dummy.commands;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.w3c.dom.Document;

@CommandDesc(name = "TagStreaming")
public class TagStreamCommand implements Command {

	boolean running = true;
	
	Log logger = LogFactory.getLog(TagStreamCommand.class);

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			Document configuration, long commandID) {

		logger.debug("TagStreaming is running!");
		while (running) {
			String rawtag = "hello\n";
			try {
				connection.sendMessage(rawtag);
				rawtag = (String) connection.receiveMessage();
				logger.debug(rawtag);
			} catch (IOException e1) {
				return CommandReturnStatus.INTERRUPTED;
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		logger.debug("TagStreaming is stopped");
		return CommandReturnStatus.SUCCESSFUL;
	}

	@Override
	public void stop() {
		running = false;
	}

}
