package org.rifidi.edeg.readerplugin.alien.command.extension;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;
import org.rifidi.edge.core.readerplugin.messages.Message;

@CommandDesc(name = "TestCommand", groups = { "Extension" })
public class TestExtensionCommand implements Command {

	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, String configuration, long commandID) {
		try {
			messageQueue.addMessage(new Message() {

				@Override
				public String toXML() {

					return "<testcommand>\nTESTCOMMAND\n</testcommand>\n";
				}
			});
		} catch (RifidiMessageQueueException e) {
			return CommandReturnStatus.INTERRUPTED;
		}
		return CommandReturnStatus.SUCCESSFUL;
	}

	@Override
	public void stop() {

	}

}
