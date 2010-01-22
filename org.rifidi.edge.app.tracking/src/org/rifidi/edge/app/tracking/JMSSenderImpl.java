/**
 * 
 */
package org.rifidi.edge.app.tracking;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.app.tracking.dao.LogicalReadersDAO;
import org.rifidi.edge.app.tracking.dao.ProductsDAO;
import org.rifidi.edge.app.tracking.domain.RFIDEvent;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

/**
 * This class is used to send message on the JMS queue.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class JMSSenderImpl implements JMSSender {

	/** JMS Destination to send messages to */
	private volatile Destination destination;
	/** JMS Template to use to send messages */
	private volatile JmsTemplate template;
	/** Data access object to look up product names from a data source */
	private volatile ProductsDAO productsDAO;
	/** Data access object to look up logical reader names from a data source */
	private LogicalReadersDAO logicalReadersDAO;
	/** Logger for this class */
	private static final Log logger = LogFactory.getLog(JMSSenderImpl.class);

	@Override
	public void arrvied(RFIDEvent event) {
		if (destination == null || template == null) {
			logger.error("JMS is not properly configured");
			return;
		}
		// form the message to send out over JMS
		final String msg = "Product "
				+ productsDAO.getProductName(event.getId())
				+ " arrived at "
				+ logicalReadersDAO.getLogicaReaderName(event.getReader(),
						event.getAntenna());
		template.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session arg0) throws JMSException {
				return arg0.createTextMessage(msg);
			}
		});
	}

	@Override
	public void departed(RFIDEvent event) {
		if (destination == null || template == null) {
			logger.error("JMS is not properly configured");
			return;
		}
		final String msg = "Product "
				+ productsDAO.getProductName(event.getId())
				+ " departed from "
				+ logicalReadersDAO.getLogicaReaderName(event.getReader(),
						event.getAntenna());
		template.send(destination, new MessageCreator() {

			@Override
			public Message createMessage(Session arg0) throws JMSException {
				return arg0.createTextMessage(msg);
			}
		});

	}

	/**
	 * Called by spring
	 * 
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * Called by spring
	 * 
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * set by spring
	 * 
	 * @param productsDAO
	 *            the productsDAO to set
	 */
	public void setProductsDAO(ProductsDAO productsDAO) {
		this.productsDAO = productsDAO;
	}

	/**
	 * Set by spring
	 * 
	 * @param logicalReadersDAO
	 *            the logicalReadersDAO to set
	 */
	public void setLogicalReadersDAO(LogicalReadersDAO logicalReadersDAO) {
		this.logicalReadersDAO = logicalReadersDAO;
	}

}
