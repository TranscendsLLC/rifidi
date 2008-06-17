package org.rifidi.edge.jms;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.BrokerService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	BrokerService broker;
	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		System.out.println("== Bundle " + this.getClass().getName() + " loaded ==");
		
		//start broker
		broker = new BrokerService();
		//TODO Save information about port and address in a configuration file
		broker.addConnector("tcp://localhost:61616");
		// Disable Presistence to avoid the creation of the ActiveMQ Directory
		broker.setPersistent(false);
		// TODO Disable JMX to avoid the use of RMI in JMS (ClassNotFoundException)
		broker.setUseJmx(false);
		broker.start();
		
		//configure and register connection factory
		ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		//TODO Save information about port and address in a configuration file
		connectionFactory.setBrokerURL("tcp://localhost:61616");
		context.registerService(ConnectionFactory.class.getName(), connectionFactory, null);

	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		broker.stop();
		System.out.println("== Bundle " + this.getClass().getName() + " stopped ==");
	}

}
