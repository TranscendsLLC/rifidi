/**
 * 
 */
package org.rifidi.edge.notifications.internal;

import javax.jms.Destination;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.api.SessionStatus;
import org.rifidi.edge.core.api.jms.notifications.CommandConfigFactoryAdded;
import org.rifidi.edge.core.api.jms.notifications.CommandConfigFactoryRemoved;
import org.rifidi.edge.core.api.jms.notifications.CommandConfigurationAddedNotification;
import org.rifidi.edge.core.api.jms.notifications.CommandConfigurationRemovedNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderAddedNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderFactoryAddedNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderFactoryRemovedNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderRemovedNotification;
import org.rifidi.edge.core.api.jms.notifications.SessionAddedNotification;
import org.rifidi.edge.core.api.jms.notifications.SessionRemovedNotification;
import org.rifidi.edge.core.api.jms.notifications.SessionStatusChangedNotification;
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
					addCommandConfiguration(serviceID);
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
				removeCommandConfiguration(serviceID);
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
			logger.warn("ReaderFactoryAdded Notification not sent " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.notifications.NotifierService#addCommandConfiguration
	 * (java.lang.String)
	 */
	@Override
	public void addCommandConfiguration(String commandConfigurationID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new CommandConfigurationAddedNotification(
									commandConfigurationID)));
		} catch (Exception e) {
			logger.warn("ReaderFactoryAdded Notification not sent " + e);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.notifications.NotifierService#removeCommandConfiguration
	 * (java.lang.String)
	 */
	@Override
	public void removeCommandConfiguration(String commandConfigurationID) {
		try {
			extNotificationTemplate.send(this.extNotificationDest,
					new NotificationMessageCreator(
							new CommandConfigurationRemovedNotification(
									commandConfigurationID)));
		} catch (Exception e) {
			logger.warn("ReaderFactoryAdded Notification not sent " + e);
		}

	}

}
