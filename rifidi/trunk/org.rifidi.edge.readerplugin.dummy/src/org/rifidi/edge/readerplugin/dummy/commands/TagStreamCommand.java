package org.rifidi.edge.readerplugin.dummy.commands;

import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.dynamicswtforms.xml.annotaions.Form;
import org.rifidi.edge.core.api.communication.Connection;
import org.rifidi.edge.core.api.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.api.messageQueue.MessageQueue;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.api.readerplugin.commands.api.Command;
import org.rifidi.edge.core.api.readerplugin.messages.impl.EnhancedTagMessage;

@Form(name = "TagStreamCommand", formElements = {})
public class TagStreamCommand implements Command {

	boolean running = true;

	private Log logger = LogFactory.getLog(TagStreamCommand.class);
	private Random random = new Random();
	

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			CommandConfiguration configuration, long commandID) {

		logger.debug("TagStreaming is running!");
		while (running) {
			byte[] id = new byte[12];
			random.nextBytes(id);
			EnhancedTagMessage etm = new EnhancedTagMessage();
			etm.setAntennaId(0);
			etm.setId(id);
			etm.setLastSeenTime(System.currentTimeMillis());
			try {
				messageQueue.addMessage(etm);
			} catch (RifidiMessageQueueException e1) {
				logger.error("ERROR", e1);
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
