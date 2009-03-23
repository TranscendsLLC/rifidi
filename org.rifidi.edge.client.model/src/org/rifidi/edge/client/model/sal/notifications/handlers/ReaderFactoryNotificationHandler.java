package org.rifidi.edge.client.model.sal.notifications.handlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.core.api.jms.notifications.ReaderFactoryNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderFactoryNotification.ReaderFactoryEventType;
import org.rifidi.edge.core.api.rmi.dto.ReaderFactoryDTO;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReaderFactory;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * A handler for incoming ReaderFactoryNotification events
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderFactoryNotificationHandler {

	/** The Notification to handle */
	private ReaderFactoryNotification notification;
	/** The map of readerFactories */
	private ObservableMap readerFactories;
	/** The serverdescription needed to make the remote call */
	private RS_ServerDescription serverDescription;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(ReaderFactoryNotificationHandler.class);

	/***
	 * Constructor
	 * 
	 * @param notification
	 *            The notification event to handle
	 * @param readerFactories
	 *            The Factories
	 * @param serverDescription
	 *            The serverdescription to use for RMI call
	 */
	public ReaderFactoryNotificationHandler(
			ReaderFactoryNotification notification,
			ObservableMap readerFactories,
			RS_ServerDescription serverDescription) {
		this.notification = notification;
		this.readerFactories = readerFactories;
		this.serverDescription = serverDescription;
	}

	/**
	 * Handle the notification
	 */
	public void handleNotification() {
		if (notification.getEventType() == ReaderFactoryEventType.ADDED) {
			// if notificaiton is add event
			Thread t = new Thread(new Runner());
			t.start();
			logger.debug("Reader Factory Added: " + notification.getId());
		} else {
			// if notificaiton is a remove event
			readerFactories.getRealm().asyncExec(new Runnable() {
				@Override
				public void run() {
					readerFactories.remove(notification.getId());
				}
			});
			logger.debug("Reader Factory Removed: " + notification.getId());
		}
	}

	/**
	 * Runnable to run when a reader factory has been added
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	private class Runner implements Runnable {
		@Override
		public void run() {
			RS_GetReaderFactory call = new RS_GetReaderFactory(
					serverDescription, notification.getId());
			final ReaderFactoryDTO dto;
			try {
				dto = call.makeCall();
				if (dto != null) {
					readerFactories.getRealm().asyncExec(new Runnable() {
						@Override
						public void run() {
							readerFactories.put(dto.getReaderFactoryID(), dto);
						}
					});
				}
			} catch (ServerUnavailable e) {
				// TODO Need to figure out a way to tell remote reader that it
				// needs to be disconnected
				e.printStackTrace();
			}

		}

	}

}
