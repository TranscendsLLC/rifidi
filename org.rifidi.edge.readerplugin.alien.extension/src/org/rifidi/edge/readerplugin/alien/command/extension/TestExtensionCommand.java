package org.rifidi.edge.readerplugin.alien.command.extension;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.exceptions.RifidiMessageQueueException;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.messages.Message;

public class TestExtensionCommand implements Command {


	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#start(org.rifidi.edge.core.communication.Connection, org.rifidi.edge.core.messageQueue.MessageQueue, org.rifidi.edge.core.messageQueue.MessageQueue, org.rifidi.edge.core.readerplugin.commands.CommandConfiguration, long)
	 */
	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, MessageQueue errorQueue,
			CommandConfiguration configuration, long commandID) {
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

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#stop()
	 */
	@Override
	public void stop() {
		// TODO Auto-generated method stub
		
	}
	
}
