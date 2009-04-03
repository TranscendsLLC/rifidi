/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import javax.jms.JMSException;
import javax.jms.Session;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.model.Activator;
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
			Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
			String ip = node.get(EdgeServerPreferences.EDGE_SERVER_IP,
					EdgeServerPreferences.EDGE_SERVER_IP_DEFAULT);
			String port = node.get(EdgeServerPreferences.EDGE_SERVER_PORT_JMS,
					EdgeServerPreferences.EDGE_SERVER_PORT_JMS_DEFAULT);
			String queuename = node.get(
					EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE,
					EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_DEFAULT);
			String tagQueueName = node.get(
					EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS,
					EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS_DEFAULT);
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
