package org.rifidi.edge.core.session.jms;

import java.io.StringWriter;
import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.session.Session;
import org.rifidi.edge.core.tag.TagRead;

public class JMSMessageThread implements Runnable {

	// Internal Thread
	private Thread thread;

	// Paramerters
	private JMSHelper jmsHelper;
	private Session session;

	// Runntime Variables
	private boolean running = false;
	private IReaderAdapter readerAdapter;

	// XML Context
	private JAXBContext context;
	private Marshaller marshaller;

	private long pollingIntervall = 1000;

	// Constructor
	public JMSMessageThread(Session session, JMSHelper jmsHelper) {
		this.jmsHelper = jmsHelper;
		this.session = session;
	}

	public boolean start() {
		if (jmsHelper != null && session != null) {

			readerAdapter = session.getAdapter();

			thread = new Thread(this, "JMSMessageThread"
					+ session.getSessionID());
			running = true;

			try {
				context = JAXBContext.newInstance(TagRead.class);
				marshaller = context.createMarshaller();
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			} catch (JAXBException e) {
				e.printStackTrace();
				return false;
			}

			thread.start();

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
				List<TagRead> tagList = readerAdapter.getNextTags();
				sendMessage(tagList);
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
