package org.rifidi.edge.client.model.sal.notifications.handlers;

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.rifidi.edge.core.api.jms.notifications.ReaderNotification;
import org.rifidi.edge.core.api.jms.notifications.ReaderNotification.ReaderEventType;
import org.rifidi.edge.core.rmi.client.readerconfigurationstub.RS_ServerDescription;

public class ReaderNotificationHandler {

	private ReaderNotification notification;
	private ObservableMap remoteReaders;
	private RS_ServerDescription serverDescription;

	private ReaderNotificationHandler(ReaderNotification notification,
			ObservableMap remoteReaders, RS_ServerDescription serverDescription) {
		this.notification = notification;
		this.remoteReaders = remoteReaders;
		this.serverDescription = serverDescription;
	}

	private void handleNotification() {
		if(notification.getEventType()==ReaderEventType.ADDED){
			
		}else{
			remoteReaders.remove(notification.getId());
		}
	}
	
	private class Runner implements Runnable{

		@Override
		public void run() {

			
		}
		
	}

}
