package org.rifidi.edge.core.services.jms;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class JMSInit {

	public JMSInit() {
		try {
			String directory = System.getProperty("activemq.base");
			System.out.println(directory);
			BrokerService externalbroker = BrokerFactory.createBroker(new URI(
					"xbean:" + directory + File.separator
							+ "rifidi-amq-external.xml"));
			externalbroker.start();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
