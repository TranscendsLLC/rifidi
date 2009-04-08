/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import javax.jms.JMSException;
import javax.jms.Session;

import org.rifidi.edge.client.model.SALModelPlugin;
import org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand;
import org.rifidi.edge.client.model.sal.preferences.EdgeServerPreferences;

/**
 * This makes a new connection to the edge server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Command_Connect implements RemoteEdgeServerCommand {

	/** The remote edge server to connect to */
	private RemoteEdgeServer remoteEdgeServer;

	/**
	 * Constructor
	 * 
	 * @param remoteEdgeServer
	 *            The remote edge server to connect to
	 */
	public Command_Connect(RemoteEdgeServer remoteEdgeServer) {
		this.remoteEdgeServer = remoteEdgeServer;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#execute
	 * ()
	 */
	@Override
	public void execute() {
		try {
			// initialize the uptime of the server to 0
			remoteEdgeServer.startupTime = 0l;
			// set up JMS consumer
			String ip = SALModelPlugin.getDefault().getPreferenceStore()
					.getString(EdgeServerPreferences.EDGE_SERVER_IP);
			String port = SALModelPlugin.getDefault().getPreferenceStore()
					.getString(EdgeServerPreferences.EDGE_SERVER_PORT_JMS);
			String queuename = SALModelPlugin.getDefault().getPreferenceStore()
					.getString(EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE);
			String tagQueueName = SALModelPlugin.getDefault()
					.getPreferenceStore().getString(
							EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS);
			remoteEdgeServer.connectionFactory.setBrokerURL("tcp://" + ip + ":"
					+ port);
			remoteEdgeServer.conn = remoteEdgeServer.connectionFactory
					.createConnection();
			remoteEdgeServer.conn.start();
			remoteEdgeServer.session = remoteEdgeServer.conn.createSession(
					false, Session.AUTO_ACKNOWLEDGE);

			remoteEdgeServer.dest = remoteEdgeServer.session
					.createQueue(queuename);
			remoteEdgeServer.consumer = remoteEdgeServer.session
					.createConsumer(remoteEdgeServer.dest);
			remoteEdgeServer.jmsMessageHandler = new JMSMessageHandler(
					remoteEdgeServer);
			remoteEdgeServer.consumer
					.setMessageListener(remoteEdgeServer.jmsMessageHandler);

			remoteEdgeServer.dest_tags = remoteEdgeServer.session
					.createQueue(tagQueueName);
			remoteEdgeServer.consumer_tags = remoteEdgeServer.session
					.createConsumer(remoteEdgeServer.dest_tags);
			remoteEdgeServer.consumer_tags
					.setMessageListener(remoteEdgeServer.jmsMessageHandler);
		} catch (JMSException e) {
			remoteEdgeServer.disconnect();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#
	 * executeEclipse()
	 */
	@Override
	public void executeEclipse() {
		remoteEdgeServer.changeState(RemoteEdgeServerState.CONNECTED);
		remoteEdgeServer.update();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.model.sal.commands.RemoteEdgeServerCommand#getType
	 * ()
	 */
	@Override
	public String getType() {
		return "CONNECT";
	}

}
