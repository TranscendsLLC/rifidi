package org.rifidi.edge.client.model.sal.notifications.handlers;

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.core.api.jms.notifications.ReaderNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderNotification.ReaderEventType;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_GetReader;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * This class handles ReaderNotificationJMS Events from the Edge Server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderNotificationHandler {

	/** The notification that was recieved */
	private ReaderNotification notification;
	/** The map of remote readers */
	private ObservableMap remoteReaders;
	/** The server description for the Reader Stub */
	private RS_ServerDescription serverDescription;

	public ReaderNotificationHandler(ReaderNotification notification,
			ObservableMap remoteReaders, RS_ServerDescription serverDescription) {
		this.notification = notification;
		this.remoteReaders = remoteReaders;
		this.serverDescription = serverDescription;
	}

	public void handleNotification() {
		// if the event is a Reader Added Event
		if (notification.getEventType() == ReaderEventType.ADDED) {
			Thread t = new Thread(new Runner(notification.getId()));
			t.start();
		} else {
			// if the event is a reader removed event
			remoteReaders.getRealm().asyncExec(new Runnable() {

				/*
				 * (non-Javadoc)
				 * 
				 * @see java.lang.Runnable#run()
				 */
				@Override
				public void run() {
					remoteReaders.remove(notification.getId());
				}

			});
		}
	}

	/**
	 * This class is an executer that runs so that the RMI call to get the
	 * Reader Information is made in a different thread than the JMS one
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	private class Runner implements Runnable {

		/** The ID of the reader */
		private String id;

		/**
		 * Constructor
		 * 
		 * @param id
		 */
		public Runner(String id) {
			this.id = id;
		}

		@Override
		public void run() {
			RS_GetReader command = new RS_GetReader(serverDescription, id);
			try {
				final ReaderDTO dto = command.makeCall();
				remoteReaders.getRealm().asyncExec(new Runnable() {

					@Override
					public void run() {
						remoteReaders.put(id, new RemoteReader(dto));
					}

				});
			} catch (ServerUnavailable e) {
				// TODO: We need to somehow notify the EdgeServer that we are
				// disconnected
			}
		}

	}

}
