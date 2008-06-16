/**
 * 
 */
package org.rifidi.edge.core.test.session;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.adapter.dummyadapter.DummyConnectionInfo;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderAdapter;
import org.rifidi.edge.core.exception.adapter.RifidiConnectionException;
import org.rifidi.edge.core.readerAdapter.AbstractConnectionInfo;
import org.rifidi.edge.core.session.Session;
import org.rifidi.edge.core.session.jms.JMSHelper;
import org.rifidi.edge.core.session.jms.JMSMessageThread;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author kyle
 * 
 */
public class SessionTest {
	
	private static final Log logger = LogFactory.getLog(SessionTest.class);

	private ConnectionFactory connectionFactory;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Make sure we can set and get a reader Adapter
	 */
	@Test
	public void testGetSetAdapter() {

		DummyReaderAdapter dummyAdapter = new DummyReaderAdapter(new DummyConnectionInfo());
		Session s = new Session(null, null, -1, null);

		s.setAdapter(dummyAdapter);
		Assert.assertEquals(dummyAdapter, s.getAdapter());

	}

	/**
	 * Make sure we can set and get a session ID
	 */
	@Test
	public void testGetSetSessionID() {
		Session session = new Session(null, null, -1, null);
		
		session.setSessionID(3);
		Assert.assertTrue(session.getSessionID() == 3);
	}

	/**	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	 * Make sure we can set and get the AbstractConnectionInfo
	 */
	@Test
	public void testGetSetAbstractConnectionInfo() {
		Session session = new Session(null, null, 0, null);
		AbstractConnectionInfo info = new DummyConnectionInfo();
		session.setConnectionInfo(info);
		Assert.assertTrue(session.getConnectionInfo() == info);
	}

	/**
	 * Make sure we can set and get the JMSThread
	 */
	@Test
	public void testJMSThread() {
		JMSHelper jmsHelper = new JMSHelper();
		
		if(connectionFactory == null)
		{
			Assert.fail("Coudn't get connection Factory out of Services");
		}
		
		jmsHelper.initializeJMSQueue(connectionFactory, "test");
		
		if (!jmsHelper.isInitialized())
		{
			Assert.fail("Error while initializing jmsHelper.");
		}
		
		DummyReaderAdapter adapter = new DummyReaderAdapter(new DummyConnectionInfo());
		
		try {
			adapter.connect();
		} catch (RifidiConnectionException e2) {
			Assert.fail();
			e2.printStackTrace();
		}
		
		JMSMessageThread jmsThread = new JMSMessageThread(1, adapter, jmsHelper);
		if(! jmsThread.start())
		{
			Assert.fail("Error while starting JMSThread");
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			MessageConsumer consumer = jmsHelper.getSession().createConsumer(jmsHelper
					.getDestination());

			// Get the next message from the queue
			Message m = consumer.receive(500);

			logger.info(((TextMessage)m).getText() + "== END ==");
			
			m = consumer.receive(5000);

			logger.info(((TextMessage)m).getText() + "== END ==");
			
		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail();
		}
		

		jmsThread.stop();

	}

	/**
	 * Make sure we can send a custom command.
	 */
	@Test
	public void testSendCustomCommand() {
		// TODO Jerry should Implement
		//Assert.fail("Not Implemented");
	}

	/**
	 * Test startTagStream and stop tag stream. If the tag stream works, we
	 * should be able to get a valid message from the JMS queue using the
	 * DummyAdapter. If the stop works, we should no longer see any messages in
	 * the queue
	 */
	@Test
	public void testStartTagStream() {

		// set up dummy connection Info
		DummyConnectionInfo info = new DummyConnectionInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(12345);
		int SessionID = 1;

		// create the dummy reader adapter
		DummyReaderAdapter readerAdapter = new DummyReaderAdapter(info);
		
		try {
			readerAdapter.connect();
		} catch (RifidiConnectionException e1) {
			// TODO Auto-generated catch block
			Assert.fail();
			e1.printStackTrace();
		}

		// create the JMS Helper
		JMSHelper jmsHelper = new JMSHelper();
		jmsHelper.initializeJMSQueue(connectionFactory, Integer
				.toString(SessionID));

		// create the JMS thread
		JMSMessageThread mthread = new JMSMessageThread(SessionID,
				readerAdapter, jmsHelper);

		// create the reader Session
		Session s = new Session(info, readerAdapter, SessionID, mthread);

		s.connect();
		
		s.startTagStream();

		Connection c;
		try {
			c = connectionFactory.createConnection();
			c.start();

			javax.jms.Session jmsSession = c.createSession(false,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
			MessageConsumer consumer = jmsSession.createConsumer(jmsHelper
					.getDestination());

			// Get the next message from the queue
			Message m = consumer.receive(500);

			System.out.println(((TextMessage)m).getText() + "== END ==");
			
			m = consumer.receive(5000);

			System.out.println(((TextMessage)m).getText() + "== END ==");
			
			// If the queue is not null, then we have started the tag stream
			// correctly
			Assert.assertNotNull(m);
			
			s.stopTagStream();
			
			// see if there is anything in the queue after it has been stopped.
			m = consumer.receive(5000);
			
			Assert.assertNull(m);

		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail();
		}

	}

	@Inject
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

}
