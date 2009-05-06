/**
 * 
 */
package org.rifidi.edge.client.model.sal;
//TODO: Comments
import javax.jms.JMSException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;

/**
 * A command that disconnects from the edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_Disconnect implements RemoteEdgeServerCommand {

	/** The remote edge server to disconnect from */
	private RemoteEdgeServer remoteEdgeServer;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(Command_Disconnect.class);

	/**
	 * Constructor
	 * 
	 * @param remoteEdgeServer
	 *            - The remote edge server to disconnect from
	 */
	public Command_Disconnect(RemoteEdgeServer remoteEdgeServer) {
		this.remoteEdgeServer = remoteEdgeServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.model.sal.RemoteEdgeServerCommand#execute()
	 */
	@Override
	public void execute() {
		// if any of the JMS objects are null, don't try to disconnect.
		if (remoteEdgeServer.conn == null || remoteEdgeServer.session == null
				|| remoteEdgeServer.consumer == null) {
			return;
		}

		// attempt to disconnect from JMS
		try {

			remoteEdgeServer.conn.stop();
			remoteEdgeServer.conn.close();
			remoteEdgeServer.session.close();
			remoteEdgeServer.consumer.close();
		} catch (JMSException e) {
			logger.warn("Error when disconnecting: " + e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.RemoteEdgeServerCommand#executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		remoteEdgeServer.readerFactories.clear();
		remoteEdgeServer.remoteReaders.clear();
		remoteEdgeServer.commandConfigFactories.clear();
		remoteEdgeServer.commandConfigurations.clear();
		remoteEdgeServer.changeState(RemoteEdgeServerState.DISCONNECTED);
		logger.info("Remote Edge Server is in the Disconnected state");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.model.sal.RemoteEdgeServerCommand#getType()
	 */
	@Override
	public String getType() {
		return "DISCONNECT";
	}

}
