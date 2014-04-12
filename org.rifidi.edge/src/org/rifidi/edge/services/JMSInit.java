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
package org.rifidi.edge.services;

import java.io.File;
import java.net.URI;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.rifidi.edge.init.URIUtility;

/**
 * This class initializes JMS by reading in a file called
 * rifidi-amq-external.xml from the "activemq.base" folder.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class JMSInit {

	public JMSInit() throws Exception {
		try {
			String directory = System.getProperty("activemq.base");
			BrokerService externalbroker = BrokerFactory.createBroker(new URI(
					URIUtility.createURI(new String("xbean:" + directory
							+ File.separator + "rifidi-amq-external.xml"))));
			externalbroker.start();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

}
