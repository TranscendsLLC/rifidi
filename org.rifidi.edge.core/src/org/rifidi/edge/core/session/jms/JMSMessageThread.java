package org.rifidi.edge.core.session.jms;

import java.io.StringWriter;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

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

	// XML Context
	private JAXBContext context;
	private Marshaller marshaller;

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

				try {
					context = JAXBContext.newInstance(TagRead.class);
					marshaller = context.createMarshaller();
					marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
							true);
				} catch (JAXBException e) {
					e.printStackTrace();
					return running;
				}
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

		}
	}

	private void sendMessage(List<TagRead> tagList) {
		for (TagRead tag : tagList) {
			StringWriter writer = new StringWriter();
			try {
				marshaller.marshal(tag, writer);
			} catch (JAXBException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			TextMessage textMessage;
			try {
				textMessage = jmsHelper.getSession().createTextMessage(
						writer.toString());
				System.out.println("++ Send next Message ++");
				System.out.println(textMessage.getText());
				jmsHelper.getMessageProducer().send(textMessage);
			} catch (JMSException e) {
				// TODO Auto-generated catch block
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
