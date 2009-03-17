/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Set;

import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.model.Activator;
import org.rifidi.edge.client.model.sal.preferences.EdgeServerPreferences;
import org.rifidi.edge.core.api.jms.notifications.ReaderNotification;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.rmi.client.commandconfigurationstub.CCServerDescription;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReaderFactories;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReaders;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * @author Kyle Neumeier
 * 
 */
public class RemoteEdgeServer implements MessageListener {

	/** The server description for the reader stub */
	private RS_ServerDescription rs_description;
	/** The server description for the command stub */
	private CCServerDescription cc_description;
	/** The set of reader factories on the server */
	private ObservableSet readerFactoryIDs;
	/** The set of command factories on the server */
	private ObservableMap commandFactoryIDs;
	/** The logger for this class */
	private static final Log logger = LogFactory.getLog(RemoteEdgeServer.class);
	/** The remote readers on this edge server */
	private ObservableMap remoteReaders;
	private ActiveMQConnectionFactory connectionFactory;

	/**
	 * Constructor
	 * 
	 * @param ip
	 *            The IP of the server
	 * @param port
	 *            The RMI port of the server
	 */
	public RemoteEdgeServer() {
		Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
		String ip = node.get(EdgeServerPreferences.EDGE_SERVER_IP, "blah");
		int port = Integer.parseInt(node.get(
				EdgeServerPreferences.EDGE_SERVER_PORT_RMI, "blah"));
		readerFactoryIDs = new WritableSet();
		commandFactoryIDs = new WritableMap();
		remoteReaders = new WritableMap();
		rs_description = new RS_ServerDescription(ip, port);
		connectionFactory = new ActiveMQConnectionFactory();
	}

	public void connect() {

		try {
			// set up JMS consumer
			Preferences node = new DefaultScope().getNode(Activator.PLUGIN_ID);
			String ip = node.get(EdgeServerPreferences.EDGE_SERVER_IP, "blah");
			String port = node.get(EdgeServerPreferences.EDGE_SERVER_PORT_JMS,
					"blah");
			String queuename = node.get(
					EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE, "blah");
			connectionFactory.setBrokerURL("tcp://" + ip + ":" + port);
			Connection conn;
			conn = connectionFactory.createConnection();
			conn.start();
			Session session = conn.createSession(false,
					Session.AUTO_ACKNOWLEDGE);
			Destination dest = session.createQueue(queuename);
			MessageConsumer consumer = session.createConsumer(dest);
			consumer.setMessageListener(this);

			// Connect to RMI
			update();

		} catch (ServerUnavailable e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * Update the model
	 * 
	 * @throws ServerUnavailable
	 *             If there was a problem when connecting to the server
	 */
	public void update() throws ServerUnavailable {
		RS_GetReaderFactories rsGetFactoriesCall = new RS_GetReaderFactories(
				rs_description);

		readerFactoryIDs.addAll(rsGetFactoriesCall.makeCall());

		RS_GetReaders rsGetReaderCall = new RS_GetReaders(rs_description);
		Set<ReaderDTO> readerDTOs = rsGetReaderCall.makeCall();
		for (ReaderDTO reader : readerDTOs) {
			logger.debug("Found reader: " + reader.getReaderID());
			RemoteReader rr = new RemoteReader();
			rr.setID(reader.getReaderID());
			remoteReaders.put(reader.getReaderID(), rr);
		}
	}

	/**
	 * @return the readerFactoryIDs
	 */
	public ObservableSet getReaderFactoryIDs() {
		return readerFactoryIDs;
	}

	/**
	 * @return the commandFactoryIDs
	 */
	public ObservableMap getCommandFactoryIDs() {
		return commandFactoryIDs;
	}

	/**
	 * @return the remoteReaders
	 */
	public ObservableMap getRemoteReaders() {
		return remoteReaders;
	}

	@Override
	public void onMessage(Message arg0) {
		try {
			if (arg0 instanceof BytesMessage) {
				final Object message = deserialize((BytesMessage) arg0);
				if (message instanceof ReaderNotification) {
					ReaderNotification ra = (ReaderNotification)message;
				}
			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private Object deserialize(BytesMessage message) throws JMSException,
			IOException, ClassNotFoundException {
		int length = (int) message.getBodyLength();
		byte[] bytes = new byte[length];
		message.readBytes(bytes);
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(
				bytes));
		return in.readObject();
	}

}
