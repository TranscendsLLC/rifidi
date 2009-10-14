package com.northwind.rfid.shipping.war;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import javax.jms.BytesMessage;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import com.northwind.rfid.shipping.notifications.AlertNotification;
import com.northwind.rfid.shipping.notifications.EZone;
import com.northwind.rfid.shipping.notifications.ItemArrivalNotification;
import com.northwind.rfid.shipping.notifications.ItemDepartureNotification;
import com.northwind.rfid.shipping.war.service.impl.TagLocationServiceManager;

/**
 * The class that implements the logic of receiving a notification from JMS
 * using the TagLocationServiceManager to update the TagLocationService
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class MessageReceiver implements MessageListener {

	/** The TagLocationServiceManager to use */
	private volatile TagLocationServiceManager TLSManager;

	/**
	 * Called by spring
	 * 
	 * @param TLSManager
	 *            The TagLocationServiceManager
	 */
	public void setTLSManager(TagLocationServiceManager TLSManager) {
		this.TLSManager = TLSManager;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.jms.MessageListener#onMessage(javax.jms.Message)
	 */
	@Override
	public void onMessage(Message arg0) {
		if (arg0 instanceof BytesMessage) {
			try {
				// deserialize the message
				Object message = deserialize((BytesMessage) arg0);
				if (message instanceof ItemArrivalNotification) {
					// If the message is an ItemArrival
					ItemArrivalNotification notification = (ItemArrivalNotification) message;
					if (notification.getZone() == EZone.DOCK_DOOR) {
						// If it arrived at the dock door
						TLSManager.PackageArrivedAtDockDoor(notification
								.getTag_Id());
					} else {
						// otherwise it arrived at the weigh station
						TLSManager.PackageArrivedAtWeighStation(notification
								.getTag_Id());
					}
				} else if (message instanceof ItemDepartureNotification) {
					// if the message is an Item Departure
					ItemDepartureNotification notification = (ItemDepartureNotification) message;
					if (notification.getZone() == EZone.DOCK_DOOR) {
						// If it departed from the dock door
						TLSManager.PackageDepartedFromDockDoor(notification
								.getTag_Id());
					} else {
						// otherwise it departed from the weigh station
						TLSManager.PackageDepartedFromWeighStation(notification
								.getTag_Id());
					}
				} else if (message instanceof AlertNotification) {
					// if the message is an alert
					TLSManager.Alert((AlertNotification) message);
				} else {
					System.out.println("Notification type not recognized");
				}
			} catch (JMSException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
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