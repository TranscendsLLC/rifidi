/**
 * 
 */
package org.rifidi.edge.core.junit.connection;

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
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.connection.jms.JMSHelper;
import org.rifidi.edge.core.connection.jms.JMSMessageThread;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.readerPlugin.AbstractReaderInfo;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.edge.readerplugin.dummy.DummyReaderInfo;
import org.rifidi.edge.readerplugin.dummy.DummyReaderPlugin;
import org.rifidi.edge.readerplugin.dummy.DummyReaderPluginFactory;
import org.rifidi.edge.readerplugin.dummy.commands.DummyCustomCommand;
import org.rifidi.edge.readerplugin.dummy.commands.DummyCustomCommandResult;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author kyle
 * 
 */
public class SessionTest {

	private static final Log logger = LogFactory.getLog(SessionTest.class);

	private ConnectionFactory connectionFactory;

	private ReaderConnectionRegistryService sessionRegistryService;

	private ReaderPluginRegistryService readerPluginRegistryService;

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

		DummyReaderPlugin dummyAdapter = new DummyReaderPlugin(
				new DummyReaderInfo());
		IReaderConnection s = new ReaderConnection(null, null, -1, null);

		s.setAdapter(dummyAdapter);
		Assert.assertEquals(dummyAdapter, s.getAdapter());

	}

	/**
	 * Make sure we can set and get a session ID
	 */
	@Test
	public void testGetSetSessionID() {
		IReaderConnection session = new ReaderConnection(null, null, -1, null);

		session.setSessionID(3);
		Assert.assertTrue(session.getSessionID() == 3);
	}

	/**
	 * @Before public void setUp() throws Exception { }
	 * 
	 * @After public void tearDown() throws Exception { } Make sure we can set
	 *        and get the AbstractConnectionInfo
	 */
	@Test
	public void testGetSetAbstractConnectionInfo() {
		IReaderConnection session = new ReaderConnection(null, null, 0, null);
		AbstractReaderInfo info = new DummyReaderInfo();
		session.setConnectionInfo(info);
		Assert.assertTrue(session.getConnectionInfo() == info);
	}

	/**
	 * Make sure we can set and get the JMSThread
	 */
	@Test
	public void testJMSThread() {
		JMSHelper jmsHelper = new JMSHelper();

		if (connectionFactory == null) {
			Assert.fail("Coudn't get connection Factory out of Services");
		}

		jmsHelper.initializeJMSQueue(connectionFactory, "test");

		if (!jmsHelper.isInitialized()) {
			Assert.fail("Error while initializing jmsHelper.");
		}

		DummyReaderPlugin adapter = new DummyReaderPlugin(
				new DummyReaderInfo());

		try {
			adapter.connect();
		} catch (RifidiConnectionException e2) {
			Assert.fail();
			e2.printStackTrace();
		}

		JMSMessageThread jmsThread = new JMSMessageThread(1, adapter, jmsHelper);
		if (!jmsThread.start()) {
			Assert.fail("Error while starting JMSThread");
		}

		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		try {
			MessageConsumer consumer = jmsHelper.getSession().createConsumer(
					jmsHelper.getDestination());

			// Get the next message from the queue
			Message m = consumer.receive(500);

			logger.info(((TextMessage) m).getText() + "== END ==");

			m = consumer.receive(5000);

			logger.info(((TextMessage) m).getText() + "== END ==");

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
		// Assert.fail("Not Implemented");

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		DummyReaderInfo info = new DummyReaderInfo();

		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();

		Assert.assertTrue("Command <Result>"
				.equals(((DummyCustomCommandResult) s
						.sendCustomCommand(new DummyCustomCommand("Command")))
						.getResult()));

		s.disconnect();

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
		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(12345);
		int SessionID = 1;

		// create the dummy reader adapter
		DummyReaderPlugin readerAdapter = new DummyReaderPlugin(info);

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
		IReaderConnection s = new ReaderConnection(info, readerAdapter,
				SessionID, mthread);

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

			System.out.println(((TextMessage) m).getText() + "== END ==");

			m = consumer.receive(5000);

			System.out.println(((TextMessage) m).getText() + "== END ==");

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

	@Inject
	public void setSessionRegistryService(ReaderConnectionRegistryService regSer) {
		this.sessionRegistryService = regSer;
	}

	@Inject
	public void setAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());
	}

}
