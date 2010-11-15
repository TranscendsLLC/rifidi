/**
 * 
 */
package org.rifidi.edge.app.tracking;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * This class is used to send message on the JMS queue.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TrackingMessageFactory {

	/** Data access object to look up product names from a data source */
	private volatile ProductsDAO productsDAO;
	/** Data access object to look up logical reader names from a data source */
	private LogicalReadersDAO logicalReadersDAO;
	/** Logger for this class */
	private static final Log logger = LogFactory
			.getLog(TrackingMessageFactory.class);

	public String getArrviedMessage(TagReadEvent event) {

		// form the message to send out over JMS
		String tagID; 
		if(event.getTag() instanceof EPCGeneration2Event){
			tagID = ((EPCGeneration2Event) event.getTag()).getEpc();
		}else{
			tagID = event.getTag().getID().toString();
		}
		String msg = productsDAO.getProductName(tagID)
				+ " arrived at "
				+ logicalReadersDAO.getLogicaReaderName(event.getReaderID(),
						event.getAntennaID());
		return msg;
	}

	public String getDepartedMessage(TagReadEvent event) {
		String tagID; 
		if(event.getTag() instanceof EPCGeneration2Event){
			tagID = ((EPCGeneration2Event) event.getTag()).getEpc();
		}else{
			tagID = event.getTag().getID().toString();
		}
		final String msg = productsDAO.getProductName(tagID)
				+ " departed from "
				+ logicalReadersDAO.getLogicaReaderName(event.getReaderID(),
						event.getAntennaID());

		return msg;

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
