/**
 * 
 */
package org.rifidi.edge.core.test;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.adapter.dummyadapter.DummyConnectionInfo;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderAdapter;
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

		DummyReaderAdapter dummyAdapter = new DummyReaderAdapter();
		Session s = new Session(null, null, -1, null);

		s.setAdapter(dummyAdapter);
		Assert.assertEquals(dummyAdapter, s.getAdapter());

	}

	/**
	 * Make sure we can set and get a session ID
	 */
	@Test
	public void testGetSetSessionID() {
		// TODO: Jerry should Implement
		Assert.fail("Not Implemented");
	}

	/**
	 * Make sure we can set and get the AbstractConnectionInfo
	 */
	@Test
	public void testGetSetAbstractConnectionInfo() {
		// TODO: Jerry should Implement
		Assert.fail("Not Implemented");
	}

	/**
	 * Make sure we can set and get the JMSThread
	 */
	@Test
	public void testGetSetJMSThread() {
		// TODO: Jerry should Implement
		Assert.fail("Not Implemented");
	}

	/**
	 * Make sure we can send a custom command.
	 */
	@Test
	public void testSendCustomCommand() {
		// TODO: Jerry should Implement
		Assert.fail("Not Implemented");
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
		DummyReaderAdapter readerAdapter = new DummyReaderAdapter();
		readerAdapter.connect();

		// create the JMS Helper
		JMSHelper jmsHelper = new JMSHelper();
		jmsHelper.initializeJMSQueue(connectionFactory, Integer
				.toString(SessionID));

		// create the JMS thread
		JMSMessageThread mthread = new JMSMessageThread(SessionID,
				readerAdapter, jmsHelper);

		// create the reader Session
		Session s = new Session(info, readerAdapter, SessionID, mthread);

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

			// If the queue is not null, then we have started the tag stream
			// correctly
			Assert.assertNotNull(m);

		} catch (JMSException e) {
			e.printStackTrace();
			Assert.fail();
		}

		// TODO: Jerry needs to call stopTagStream and then make sure we don't
		// have any more messages on the queue
		// have any new messages in the JMS queue.
		Assert.fail("Test not complete");

	}

	@Inject
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

}
