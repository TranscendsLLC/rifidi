/**
 * 
 */
package org.rifidi.edge.core.test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.session.jms.JMSHelper;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author kyle
 * 
 */
public class JMSHelperTest {

	private ConnectionFactory connectionFactory;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
	}
	
	
	/**
	 * Test to make sure set and get work for Connections
	 */
	@Test
	public void testSetGetConnection() {
		
		
		JMSHelper helper = new JMSHelper();
		
		Connection c = null;
		try {
			c = connectionFactory.createConnection();
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create connection failed.");
		}
		
		helper.setConnection(c);
		
		Assert.assertTrue(helper.getConnection() == c);
		
		//Assert.fail("Not Implemented");
	}
	
	/**
	 * Test to make sure set and get work for Session
	 */
	@Test
	public void testSetGetSession(){
		
		
		JMSHelper helper = new JMSHelper();
		
		Connection c = null;
		try {
			c = connectionFactory.createConnection();
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create connection failed.");
		}
		
		javax.jms.Session jmsSession = null;
		try {
			jmsSession = c.createSession(false,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create session failed.");
		}
		
		helper.setSession(jmsSession);
		Assert.assertTrue(helper.getSession() == jmsSession) ;
	}

	/**
	 * Test to make sure set and get work for Destinations
	 */
	@Test
	public void testSetGetDestination() {
		
		JMSHelper helper = new JMSHelper();
		
		Connection c = null;
		try {
			c = connectionFactory.createConnection();
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create connection failed.");
		}
		
		javax.jms.Session jmsSession = null;
		try {
			jmsSession = c.createSession(false,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create session failed.");
		}
		
		Destination d = null;
		try {
			 d = jmsSession.createQueue("test");
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create Destination failed.");
		}
		
		helper.setDestination(d);
		
		Assert.assertTrue(d == helper.getDestination());
		
		//Assert.fail("Not Implemented");
	}

	/**
	 * Test to make sure set and get work for message Producer
	 */
	@Test
	public void testSetGetMessageProducer() {
		//TODO: Jerry should Implement
		
		JMSHelper helper = new JMSHelper();
		
		Connection c = null;
		try {
			c = connectionFactory.createConnection();
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create connection failed.");
		}
		
		javax.jms.Session jmsSession = null;
		try {
			jmsSession = c.createSession(false,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create session failed.");
		}
		
		Destination d = null;
		try {
			 d = jmsSession.createQueue("test");
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create Destination failed.");
		}
		
		MessageProducer messageProducer = null;
		try {
			messageProducer = jmsSession.createProducer(d);
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail("JMS: create MessageProducer failed.");
		}
		
		helper.setMessageProducer(messageProducer);
		Assert.assertTrue(helper.getMessageProducer() == messageProducer);
		
	}

	/**
	 * Test to make sure set and test work for Initialized
	 */
	@Test
	public void testSetIsInitialized() {
		JMSHelper helper = new JMSHelper();
		
		helper.initializeJMSQueue(connectionFactory, "test");
		
		Assert.assertTrue(helper.isInitialized());
	}

	/**
	 * Test to make sure Initialize works. Output from method should be true.
	 * All objects (connection, Destination, producer, and session should not
	 * be null)
	 */
	@Test
	public void testInitialize() {

		//Assert.fail("Not Implemented");
		
		JMSHelper helper = new JMSHelper();
		
		helper.initializeJMSQueue(connectionFactory, "test");
		
		Assert.assertNotNull(helper.getSession());
		Assert.assertNotNull(helper.getDestination());
		Assert.assertNotNull(helper.getMessageProducer());
		Assert.assertNotNull(helper.getSession());

	}
	
	@Inject
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}
}
