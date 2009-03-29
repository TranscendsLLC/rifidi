/**
 * 
 */
package org.rifidi.edge.notifications.internal;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.jms.notifications.ReaderAddedNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderFactoryAddedNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderFactoryRemovedNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderRemovedNotification;
import org.rifidi.edge.core.api.jms.notifications.SessionAddedNotification;
import org.rifidi.edge.core.api.jms.notifications.SessionRemovedNotification;
import org.rifidi.edge.core.daos.CommandDAO;
import org.rifidi.edge.core.daos.ReaderDAO;
import org.rifidi.edge.core.notifications.NotifierService;
import org.springframework.jms.core.JmsTemplate;

/**
 * This object is instantiated by spring and listens for certain events to
 * happen (such as Readers added to the edge server), and then sends out JMS
 * notifications
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class NotifierServiceImpl implements NotifierService {

	/** The template for sending out Notification messages */
	private JmsTemplate extNotificationTemplate;
	/** The queue to send out notifications on */
	private Destination extNotificationDest;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(NotifierServiceImpl.class);
	/** reader dao. Won't be needed once we have aspects */
	private ReaderDAO readerDAO;
	/** commanddao. won't be needed once we have aspects */
	private CommandDAO commandDAO;

	/**
	 * Called by spring
	 * 
	 * @param readerDAO
	 *            the readerDAO to set
	 */
	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	/**
	 * called by spring
	 * 
	 * @param commandDAO
	 *            the commandDAO to set
	 */
	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}

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
	 * @see org.rifidi.edge.notifications.NotifierService#addReaderEvent(java
	 * .lang.String)
	 */
	@Override
	public void addConfigurationEvent(String serviceID) {
		try {
			if (readerDAO != null) {
				if (null != readerDAO.getReaderByID(serviceID)) {
					extNotificationTemplate.send(this.extNotificationDest,
							new NotificationMessageCreator(
									new ReaderAddedNotification(serviceID)));
					return;
				}
			}
			if (commandDAO != null) {
				if (null != commandDAO.getCommandByID(serviceID)) {
					// TODO: send here
					return;
				}
			}

		} catch (Exception e) {
			logger.warn("Configuration Added Notification not sent: " + e);
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
	 * @see org.rifidi.edge.notifications.NotifierService#removeReaderEvent(
	 * java.lang.String)
	 */
	@Override
	public void removeConfigurationEvent(String serviceID) {
		if (readerDAO != null) {
			if (null != readerDAO.getReaderByID(serviceID)) {
				extNotificationTemplate.send(this.extNotificationDest,
						new NotificationMessageCreator(
								new ReaderRemovedNotification(serviceID)));
				return;
			}
		}
		if (commandDAO != null) {
			if (null != commandDAO.getCommandByID(serviceID)) {
				//TODO: send notification
				return;
			}
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
}
