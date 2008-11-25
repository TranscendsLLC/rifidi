package org.rifidi.edge.dummy.extension;

import org.rifidi.edge.core.communication.Connection;
import org.rifidi.edge.core.messageQueue.MessageQueue;
import org.rifidi.edge.core.readerplugin.commands.Command;
import org.rifidi.edge.core.readerplugin.commands.CommandReturnStatus;
import org.rifidi.edge.core.readerplugin.commands.annotations.CommandDesc;

/**
 * This is a dummy extension for a command
 * 
 * @author Andreas Huebner - andreas@pramari.com
 *
 */
@CommandDesc(name = "Test")
public class TestCommand implements Command {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.readerplugin.commands.Command#start(org.rifidi.edge.core.communication.Connection, org.rifidi.edge.core.messageQueue.MessageQueue, java.lang.String, long)
	 */
	@Override
	public CommandReturnStatus start(Connection connection,
			MessageQueue messageQueue, String configuration, long commandID) {
		// TODO Auto-generated method stub
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
