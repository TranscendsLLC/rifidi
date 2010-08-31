package org.rifidi.edge.core.services.jms;

import java.io.File;
import java.net.URI;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;
import org.rifidi.edge.init.utility.URIUtility;

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
