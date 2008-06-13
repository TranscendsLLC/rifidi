package org.rifidi.edge.core.session.jms;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.tag.TagRead;

public class JMSMessageThread implements Runnable {

	// Internal Thread
	private Thread thread;

	// Paramerters
	private JMSHelper jmsHelper;
	private int sessionID;
	private IReaderAdapter readerAdapter;

	// Runntime Variables
	private boolean running = false;

	// Polling time if ReaderAdapter is non blocking
	private long pollingIntervall = 1000;

	// Constructor
	public JMSMessageThread(int sessionID, IReaderAdapter readerAdapter,
			JMSHelper jmsHelper) {
		this.jmsHelper = jmsHelper;
		this.sessionID = sessionID;
		this.readerAdapter = readerAdapter;
	}

	public boolean start() {
		if (jmsHelper != null) {
			if (jmsHelper.isInitialized()) {

				thread = new Thread(this, "JMSMessageThread" + sessionID);

				running = true;
				thread.start();
			}
		}
		return running;
	}

	public void stop() {
		running = false;
		if (thread.isAlive())
			thread.interrupt();
	}

	@Override
	public void run() {
		try {
			while (running) {
				// Get the tags form the reader adapter
				List<TagRead> tagList = readerAdapter.getNextTags();
				// Put the received tags on the JMS Queue aboard if tags == null
				if (tagList == null) {
					running = false;
					break;
				}
				sendMessage(tagList);
				// Wait for the polling interval to time out if reader adapter
				// is not blocking
				if (!readerAdapter.isBlocking())
					Thread.sleep(pollingIntervall);
			}
		} catch (InterruptedException e) {
			//TODO: Deal with exception.
			running = false;
		} catch (RifidiAdapterIllegalStateException e) {
			//TODO: Deal with exception.
			running = false;
		}
	}

	private void sendMessage(List<TagRead> tagList) {
		for (TagRead tag : tagList) {
			TextMessage textMessage;
			try {
				textMessage = jmsHelper.getSession().createTextMessage(
						tag.toXML());
				jmsHelper.getMessageProducer().send(textMessage);
			} catch (JMSException e) {
				// TODO Think about error handling
				e.printStackTrace();
			}
		}
	}

	public long getPollingIntervall() {
		return pollingIntervall;
	}

	public void setPollingIntervall(long pollingIntervall) {
		this.pollingIntervall = pollingIntervall;
	}
}
