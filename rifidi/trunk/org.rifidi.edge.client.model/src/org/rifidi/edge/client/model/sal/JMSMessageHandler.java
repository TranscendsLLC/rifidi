
package org.rifidi.edge.client.model.sal;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.model.sal.commands.RequestExecuterSingleton;
import org.rifidi.edge.api.jms.notifications.CommandConfigFactoryAdded;
import org.rifidi.edge.api.jms.notifications.CommandConfigFactoryRemoved;
import org.rifidi.edge.api.jms.notifications.CommandConfigurationAddedNotification;
import org.rifidi.edge.api.jms.notifications.CommandConfigurationRemovedNotification;
import org.rifidi.edge.api.jms.notifications.JobDeletedNotification;
import org.rifidi.edge.api.jms.notifications.JobSubmittedNotification;
import org.rifidi.edge.api.jms.notifications.PropertyChangedNotification;
import org.rifidi.edge.api.jms.notifications.ReaderAddedNotification;
import org.rifidi.edge.api.jms.notifications.ReaderFactoryAddedNotification;
import org.rifidi.edge.api.jms.notifications.ReaderFactoryRemovedNotification;
import org.rifidi.edge.api.jms.notifications.ReaderRemovedNotification;
import org.rifidi.edge.api.jms.notifications.SessionAddedNotification;
import org.rifidi.edge.api.jms.notifications.SessionRemovedNotification;
import org.rifidi.edge.api.jms.notifications.SessionStatusChangedNotification;
import org.rifidi.edge.api.tags.TagBatch;

/**
 * An object who listens to a JMS Queue and schedules commands based on the kind
 * of Notification message that is received
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class JMSMessageHandler implements MessageListener {

	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(JMSMessageHandler.class);
	/** The remote edge server */
	private RemoteEdgeServer server;

	/***
	 * Constructor
	 * 
	 * @param server
	 *            The remote edge server
	 */
	public JMSMessageHandler(RemoteEdgeServer server) {
		this.server = server;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message arg0) {
		if (arg0 instanceof BytesMessage) {
			Object message;
			try {
				message = deserialize((BytesMessage) arg0);
				if (message instanceof TagBatch) {
					TagBatch batch = (TagBatch) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_TagBatchReceived(server, batch));
				} else if (message instanceof ReaderAddedNotification) {
					ReaderAddedNotification notification = (ReaderAddedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_ReaderAdded(server, notification));
				} else if (message instanceof ReaderRemovedNotification) {
					ReaderRemovedNotification notification = (ReaderRemovedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_ReaderRemoved(server, notification));
				} else if (message instanceof ReaderFactoryAddedNotification) {
					ReaderFactoryAddedNotification notification = (ReaderFactoryAddedNotification) message;
					RequestExecuterSingleton.getInstance()
							.scheduleRequest(
									new Command_ReaderFactoryAdded(server,
											notification));
				} else if (message instanceof ReaderFactoryRemovedNotification) {
					ReaderFactoryRemovedNotification notification = (ReaderFactoryRemovedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_ReaderFactoryRemoved(server,
									notification));
				} else if (message instanceof SessionAddedNotification) {
					SessionAddedNotification notification = (SessionAddedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_SessionAdded(server, notification));
				} else if (message instanceof SessionRemovedNotification) {
					SessionRemovedNotification notification = (SessionRemovedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_SessionRemoved(server, notification));
				} else if (message instanceof SessionStatusChangedNotification) {
					SessionStatusChangedNotification notification = (SessionStatusChangedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_SessionStatusChanged(server,
									notification));
				} else if (message instanceof CommandConfigFactoryAdded) {
					CommandConfigFactoryAdded notification = (CommandConfigFactoryAdded) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_CommandConfigFactoryAdded(server,
									notification));
				} else if (message instanceof CommandConfigFactoryRemoved) {
					CommandConfigFactoryRemoved notification = (CommandConfigFactoryRemoved) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_CommandConfigFactoryRemoved(server,
									notification));
				} else if (message instanceof CommandConfigurationAddedNotification) {
					CommandConfigurationAddedNotification notification = (CommandConfigurationAddedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_CommandConfigurationAdded(server,
									notification));

				} else if (message instanceof CommandConfigurationRemovedNotification) {
					CommandConfigurationRemovedNotification notification = (CommandConfigurationRemovedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_CommandConfigurationRemoved(server,
									notification));
				} else if (message instanceof JobSubmittedNotification) {
					JobSubmittedNotification notification = (JobSubmittedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_JobSubmitted(server, notification));
				} else if (message instanceof JobDeletedNotification) {
					JobDeletedNotification notification = (JobDeletedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_JobDeleted(server, notification));
				} else if (message instanceof PropertyChangedNotification) {
					PropertyChangedNotification notification = (PropertyChangedNotification) message;
					RequestExecuterSingleton.getInstance().scheduleRequest(
							new Command_PropertyChanged(server, notification));
				}
			} catch (JMSException e) {
				logger.warn("JMS Exception while recieving message");
				server.disconnect();
			} catch (IOException e) {
				logger.warn("Cannot Deserialize the Message " + arg0);
			} catch (ClassNotFoundException e) {
				logger.warn("Cannot Deserialize the Message " + arg0);
			}
		}

	}

	/***
	 * A private helper method that deserializes the incoming JMS notifications
	 * 
	 * @param message
	 *            The message to deserialize
	 * @return
	 * @throws JMSException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
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
