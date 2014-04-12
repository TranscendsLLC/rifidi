/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.tools.tracking;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.notification.EPCGeneration2Event;
import org.rifidi.edge.notification.TagReadEvent;

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
