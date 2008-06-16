package org.rifidi.edge.core.session.jms;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapterenums.EReaderAdapterState;
import org.rifidi.edge.core.session.Session;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class JMSMessageThread implements Runnable {

	private Log logger = LogFactory.getLog(JMSMessageThread.class);
	
	// Internal Thread
	private Thread thread;

	// Paramerters
	private JMSHelper jmsHelper;
	private int sessionID;
	private IReaderAdapter readerAdapter;

	// SessionRegisrtyService
	private SessionRegistryService sessionRegistryService;

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
		
		ServiceRegistry.getInstance().service(this);
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
			// This should be fine
			running = false;
		} catch (RifidiAdapterIllegalStateException e) {
			// this is not the best solution... maybe there is another way to deal with this.
			if(sessionRegistryService != null)
			{
				Session session = sessionRegistryService.getReaderSession(sessionID);
				session.setErrorCause(e);
			}
			running = false;
		} catch (RuntimeException e){
			// this is not the best solution... maybe there is another way to deal with this.
			/* Error Resistance.
			 * Uncaught Runtime errors should not cause the whole
			 * edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to 
			 * be thrown up the stack.
			 */
			if(sessionRegistryService != null)
			{
				Session session = sessionRegistryService.getReaderSession(sessionID);
				session.setErrorCause(e);
			}
			logger.error("Uncaught RuntimeException in " + readerAdapter.getClass() + " adapter. " +
						 "This means that there may be an unfixed bug in the adapter.", e);
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

	/**
	 * @param sessionRegistryService
	 *            the sessionRegistryService to set
	 */
	@Inject
	public void setSessionRegistryService(
			SessionRegistryService sessionRegistryService) {
		this.sessionRegistryService = sessionRegistryService;
	}
}
