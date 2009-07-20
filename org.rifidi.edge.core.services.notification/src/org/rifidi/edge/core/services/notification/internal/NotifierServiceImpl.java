package org.rifidi.edge.core.services.notification.internal;

import java.util.Map;

import javax.jms.Destination;
import javax.management.AttributeList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.configuration.ConfigurationType;
import org.rifidi.configuration.RifidiService;
import org.rifidi.edge.api.SessionStatus;
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
import org.rifidi.edge.core.services.notification.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * This object is instantiated by spring and listens for certain events to
 * happen (such as Readers added to the edge server), and then sends out JMS
 * notifications
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class NotifierServiceImpl implements NotifierService {

	/** The template for sending out Notification messages */
	private JmsTemplate extNotificationTemplate;
	/** The queue to send out notifications on */
	private Destination extNotificationDest;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(NotifierServiceImpl.class);

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

	@Override
	public void addCommandEvent(String commandID) {
		try {
			extNotificationTemplate
					.send(this.extNotificationDest,
							new NotificationMessageCreator(
									new CommandConfigurationAddedNotification(
											commandID)));
		} catch (Exception e) {
			logger.warn("commandconfiguraitonAdded Notification not sent " + e);
		}

	}

	@Override
	public void addReaderEvent(String readerID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(new ReaderAddedNotification(
							readerID)));
		} catch (Exception e) {
			logger.warn("ReaderAddNotification not sent + e");
		}

	}

	@Override
	public void removeCommandEvent(String commandID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new CommandConfigurationRemovedNotification(
									commandID)));
		} catch (Exception e) {
			logger.warn("CommandConfiguraitonRemoved Notification not sent "
					+ e);
		}

	}

	@Override
	public void removeReaderEvent(String readerID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new ReaderRemovedNotification(readerID)));
		} catch (Exception e) {
			logger.warn("Remove Reader Event not sent " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.notifications.NotifierService#addSessionEvent(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void addSessionEvent(String readerID, String sessionID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new SessionAddedNotification(readerID, sessionID)));
		} catch (Exception e) {
			logger.warn("Session Added Notification not sent: " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.notifications.NotifierService#removeSessionEvent
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void removeSessionEvent(String readerID, String sessionID) {
		try {
			extNotificationTemplate
					.send(this.extNotificationDest,
							new NotificationMessageCreator(
									new SessionRemovedNotification(readerID,
											sessionID)));
		} catch (Exception e) {
			logger.warn("Session Removed Notification not sent: " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.notifications.NotifierService#addReaderFactoryEvent
	 * (java.lang.String)
	 */
	@Override
	public void addReaderFactoryEvent(String readerFactoryID) {
		try {
			extNotificationTemplate
					.send(this.extNotificationDest,
							new NotificationMessageCreator(
									new ReaderFactoryAddedNotification(
											readerFactoryID)));
		} catch (Exception e) {
			logger.warn("ReaderFactoryAdded Notification not sent " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.notifications.NotifierService#removeReaderFactoryEvent
	 * (java.lang.String)
	 */
	@Override
	public void removeReaderFactoryEvent(String readerFactoryID) {
		extNotificationTemplate.send(this.extNotificationDest,
				new NotificationMessageCreator(
						new ReaderFactoryRemovedNotification(readerFactoryID)));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.notifications.NotifierService#sessionStatusChanged
	 * (java.lang.String, java.lang.String,
	 * org.rifidi.edge.core.api.SessionStatus)
	 */
	@Override
	public void sessionStatusChanged(String readerID, String sessionID,
			SessionStatus sessionStatus) {
		extNotificationTemplate.send(this.extNotificationDest,
				new NotificationMessageCreator(
						new SessionStatusChangedNotification(readerID,
								sessionID, sessionStatus)));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.notifications.NotifierService#
	 * addCommandConfigFactoryEvent(java.lang.String, java.util.Set)
	 */
	@Override
	public void addCommandConfigFactoryEvent(String readerFactoryID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new CommandConfigFactoryAdded(readerFactoryID)));
		} catch (Exception e) {
			logger.warn("ReaderFactoryAdded Notification not sent " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.notifications.NotifierService#
	 * removeCommandConfigFactoryEvent(java.lang.String)
	 */
	@Override
	public void removeCommandConfigFactoryEvent(String readerFactoryID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new CommandConfigFactoryRemoved(readerFactoryID)));
		} catch (Exception e) {
			logger.warn("ReaderFactoryRemoved Notification not sent " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.notifications.NotifierService#jobDeleted(java.lang
	 * .String, java.lang.String, java.lang.Integer)
	 */
	@Override
	public void jobDeleted(String readerID, String sessionID, Integer jobID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(new JobDeletedNotification(
							readerID, sessionID, jobID)));
		} catch (Exception e) {
			logger.warn("job deleted Notification not sent " + e);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.notifications.NotifierService#jobSubmitted(java.
	 * lang.String, java.lang.String, java.lang.Integer, java.lang.String)
	 */
	@Override
	public void jobSubmitted(String readerID, String sessionID, Integer jobID,
			String commandID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new JobSubmittedNotification(readerID, sessionID,
									jobID, commandID)));
		} catch (Exception e) {
			logger.warn("job submitted Notification not sent " + e);

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.services.notification.NotifierService#attributesChanged
	 * (java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public void attributesChanged(String configurationID,
			AttributeList attributes) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new PropertyChangedNotification(configurationID,
									attributes)));
		} catch (Exception e) {
			logger.warn("property changed Notification not sent " + e);

		}

	}

	public void register(RifidiService config, Map<String, String> properties) {
		if (properties.containsKey("type")
				&& properties.get("type").equals(
						ConfigurationType.READER.toString())) {
			addReaderEvent(config.getID());
			return;
		}
		if (properties.containsKey("type")
				&& properties.get("type").equals(
						ConfigurationType.COMMAND.toString())) {
			addCommandEvent(config.getID());
			return;
		}
	}

	/**
	 * Called whenever a Configuration got removed from OSGi.
	 * 
	 * @param config
	 * @param properties
	 */
	public void unregister(RifidiService config, Map<String, String> properties) {
		if (properties.containsKey("type")
				&& properties.get("type").equals(
						ConfigurationType.READER.toString())) {
			removeReaderEvent(config.getID());
		}
		if (properties.containsKey("type")
				&& properties.get("type").equals(
						ConfigurationType.COMMAND.toString())) {
			removeCommandEvent(config.getID());
			return;
		}
	}
}
