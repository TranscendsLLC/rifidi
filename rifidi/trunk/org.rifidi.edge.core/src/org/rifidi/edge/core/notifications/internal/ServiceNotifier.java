/**
 * 
 */
package org.rifidi.edge.core.notifications.internal;

import java.util.Dictionary;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.Configuration;
import org.rifidi.edge.core.api.jms.notifications.ReaderNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderNotification.ReaderEventType;
import org.rifidi.edge.core.commands.AbstractCommandConfiguration;
import org.rifidi.edge.core.commands.AbstractCommandConfigurationFactory;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.readers.AbstractReader;
import org.rifidi.edge.core.readers.AbstractReaderFactory;
import org.springframework.jms.core.JmsTemplate;

/**
 * This object is instantiated by spring and listens for certain events to
 * happen (such as Readers added to the edge server), and then sends out JMS
 * notifications
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ServiceNotifier {

	/** The template for sending out Notification messages */
	private JmsTemplate extNotificationTemplate;
	/** The queue to send out notifications on */
	private Destination extNotificationDest;
	/** The Reader DAO */
	private ReaderDAO readerDAO;
	/** The Command DAO */
	private CommandDAO commandDAO;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(ServiceNotifier.class);

	/**
	 * Called by Spring
	 * 
	 * @param exextNotificationTemplate
	 *            the exextNotificationQueue to set
	 */
	public void setExtNotificationTemplate(JmsTemplate extNotificationTemplate) {
		this.extNotificationTemplate = extNotificationTemplate;
	}

	/**
	 * called by Spring
	 * 
	 * @param extNotificationDest
	 *            the extNotificationDest to set
	 */
	public void setExtNotificationDest(Destination extNotificationDest) {
		this.extNotificationDest = extNotificationDest;
	}

	/**
	 * Called by Spring
	 * 
	 * @param readerDAO
	 *            the readerDAO to set
	 */
	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	/**
	 * Called by Spring
	 * 
	 * @param commandDAO
	 *            the commandDAO to set
	 */
	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}

	/**
	 * Called by spring whenever a new configuration is added to the
	 * configuration service
	 * 
	 * @param configuration
	 */
	public void bindConfiguration(Configuration configuration,
			Dictionary<String, String> parameters) {
		AbstractReader<?> reader = readerDAO.getReaderByID(configuration
				.getServiceID());
		if (reader != null) {
			try {
				extNotificationTemplate.send(this.extNotificationDest,
						new NotificationMessageCreator(new ReaderNotification(reader
								.getID(), ReaderEventType.ADDED)));
			} catch (Exception e) {
				logger.warn("Reader Added Notification not sent: " + e);
			}
		}
		AbstractCommandConfiguration<?> command = commandDAO
				.getCommandByID(configuration.getServiceID());
		if (command != null) {
			// TODO: send JMS notification
			return;
		}
	}

	/**
	 * Called by spring whenever a Configuration is removed
	 * 
	 * @param configuration
	 */
	public void unbindConfiguration(Configuration configuration,
			Dictionary<String, String> parameters) {
		AbstractReader<?> reader = readerDAO.getReaderByID(configuration
				.getServiceID());
		if (reader != null) {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(new ReaderNotification(reader
							.getID(), ReaderEventType.REMOVED)));
		}
		AbstractCommandConfiguration<?> command = commandDAO
				.getCommandByID(configuration.getServiceID());
		if (command != null) {
			// TODO: send JMS notification
			return;
		}
	}

	/**
	 * Called by spring whenever a ReaderFactory is added
	 * 
	 * @param factory
	 */
	public void bindReaderFactory(AbstractReaderFactory<?> factory,
			Dictionary<String, String> parameters) {
		// TODO: send JMS notification
	}

	/**
	 * Called by spring whenever a ReaderFactory is removed
	 * 
	 * @param factory
	 */
	public void unbindReaderFactory(AbstractReaderFactory<?> factory,
			Dictionary<String, String> parameters) {
		// TODO: send JMS notification
	}

	/**
	 * Called by spring whenever a CommandFactory is added
	 * 
	 * @param factory
	 */
	public void bindCommandFactory(AbstractCommandConfigurationFactory factory,
			Dictionary<String, String> parameters) {
		// TODO: send JMS notification
	}

	/**
	 * Called by spring whenever a CommandFactory is removed
	 * 
	 * @param factory
	 */
	public void unbindCommandFactory(
			AbstractCommandConfigurationFactory factory,
			Dictionary<String, String> parameters) {
		// TODO: send JMS notification
	}

}
