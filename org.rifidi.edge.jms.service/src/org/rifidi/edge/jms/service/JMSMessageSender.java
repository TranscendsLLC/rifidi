package org.rifidi.edge.jms.service;

import java.util.List;

import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.common.utilities.thread.AbstractThread;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.registry.ReaderConnectionRegistryService;
import org.rifidi.edge.core.exception.RifidiException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class JMSMessageSender extends AbstractThread {

	private Log logger = LogFactory.getLog(JMSMessageSender.class);

	// Paramerters
	private JMSHelper jmsHelper;
	private int readerConnectionID;
	private IReaderPlugin readerAdapter;

	// SessionRegisrtyService
	private ReaderConnectionRegistryService connectionRegistryService;


	// Polling time if ReaderAdapter is non blocking
	private long pollingIntervall = 1000;

	
	/**
	 * Constructs the message thread.
	 * @param connectionID The ID of the connection
	 * @param readerAdapter The reader adapter plugin
	 * @param jmsHelper A JMS Helper object 
	 */
	public JMSMessageSender(int connectionID, IReaderPlugin readerAdapter,
			JMSHelper jmsHelper) {
		super( "JMSMessageThread "
						+ connectionID);
		
		this.jmsHelper = jmsHelper;
		this.readerConnectionID = connectionID;
		this.readerAdapter = readerAdapter;

		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * Starts this thread.
	 * @return If the starting is successful or not.
	 */
	public boolean start() {
		if (jmsHelper != null) {
			if (jmsHelper.isInitialized()) {
				logger.debug("JMS Message Thread started for Queue: " + readerConnectionID);
				running = true;
				start();
			}
		}
		return running;
	}

	/**
	 * Stop this thread.
	 */
/*	public void stop() {
		running = false;
		if (thread.isAlive())
			thread.interrupt();
	} 
*/

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			while (running) {
				// Get the tags form the reader adapter
				List<TagRead> tagList = readerAdapter.getNextTags();
				//logger.debug(tagList.size() + " new Tags. Storing it at the Queue");
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
		} catch (RifidiConnectionIllegalStateException e) {
			// this is not the best solution... maybe there is another way to
			// deal with this.
			if (connectionRegistryService != null) {
				logger.debug("sessionRegistryService is not null");
				IReaderConnection session = connectionRegistryService
						.getReaderConnection(readerConnectionID);
				session.setErrorCause(e);
			}
			running = false;
		} catch (JMSException e) {
			String errorMsg = "Error trying to send tags to client over JMS";
		
			if (connectionRegistryService != null) {
				IReaderConnection connection = connectionRegistryService
						.getReaderConnection(readerConnectionID);
				connection.setErrorCause(new RifidiConnectionIllegalStateException(errorMsg , e));
			}
		
			logger.error(errorMsg, e);
			running = false;		
		} catch (RuntimeException e) {
			//TODO this is not the best solution... maybe there is another way to
			// deal with this.
			/*
			 * Error Resistance. Uncaught Runtime errors should not cause the
			 * whole edge server to go down. Only that adapter that caused it.
			 * Reminder: Runtime errors in java do not need a "throws" clause to
			 * be thrown up the stack.
			 */
			
			String errorMsg = "Uncaught RuntimeException in " + readerAdapter.getClass()
				+ " adapter. This means that there may be an unfixed bug in the adapter.";
			
			if (connectionRegistryService != null) {
				IReaderConnection connection = connectionRegistryService
						.getReaderConnection(readerConnectionID);
				connection.setErrorCause(new RifidiException(errorMsg , e));
			}
			
			logger.error(errorMsg, e);
			running = false;
		}
	}

	/**
	 * Send the tag list though JMS
	 * @param tagList The tag list to send.
	 */
	private void sendMessage(List<TagRead> tagList) throws JMSException {
		for (TagRead tag : tagList) {
			TextMessage textMessage;

				textMessage = jmsHelper.getSession().createTextMessage(
						tag.toXML());
				textMessage.setJMSExpiration(1000);
				jmsHelper.getMessageProducer().send(textMessage);

		}
	}

	/**
	 * @return
	 */
	public long getPollingIntervall() {
		return pollingIntervall;
	}

	/**
	 * @param pollingIntervall
	 */
	public void setPollingIntervall(long pollingIntervall) {
		this.pollingIntervall = pollingIntervall;
	}

	/**
	 * Edge Server injection method. Sets an internal variable.
	 * @param sessionRegistryService
	 */
	@Inject
	public void setConnectionRegistryService(
			ReaderConnectionRegistryService connectionRegistryService) {
		this.connectionRegistryService = connectionRegistryService;
	}
}
