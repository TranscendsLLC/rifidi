/**
 * 
 */
package org.rifidi.edge.core.junit.connection;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.impl.ReaderConnection;
import org.rifidi.edge.core.exception.RifidiException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.service.communication.CommunicationService;
import org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.service.readerplugin.ReaderPluginRegistryService;
import org.rifidi.edge.jms.service.helper.JMSHelper;
import org.rifidi.edge.readerplugin.dummy.DummyProtocol;
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

	// private static final Log logger = LogFactory.getLog(SessionTest.class);

	private ConnectionFactory connectionFactory;

	private ReaderConnectionRegistryService sessionRegistryService;

	private ReaderPluginRegistryService readerPluginRegistryService;

	private CommunicationService communicationService;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		System.out.println(CommunicationServiceImpl.class.getName());
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
		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(10000);

		DummyReaderPlugin dummyAdapter = new DummyReaderPlugin(info);
		IReaderConnection s = new ReaderConnection(null, null, null, -1);

		s.setAdapter(dummyAdapter);
		Assert.assertEquals(dummyAdapter, s.getAdapter());
		dummyAdapter.dispose();
	}

	/**
	 * Make sure we can set and get a session ID
	 */
	@Test
	public void testGetSetSessionID() {
		IReaderConnection session = new ReaderConnection(null, null, null, -1);

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

		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(10005);
		IReaderConnection session = new ReaderConnection(info,
				new DummyReaderPlugin(info), null, 0);
		session.setConnectionInfo(info);
		Assert.assertTrue(session.getConnectionInfo() == info);

	}

	/**
	 * Make sure we can send a custom command.
	 */
	@Test
	public void testSendCustomCommand() {

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(10002);

		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		try {
			s.connect();
		} catch (RifidiException e) {
			e.printStackTrace();
			Assert.fail();
		}

		try {
			Assert
					.assertTrue("Command <Result>"
							.equals(((DummyCustomCommandResult) s
									.sendCustomCommand(new DummyCustomCommand(
											"Command"))).getResult()));
		} catch (RifidiException e) {
			e.printStackTrace();
			Assert.fail();
		}

		try {
			s.disconnect();
		} catch (RifidiException e) {
			e.printStackTrace();
			Assert.fail();
		}

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
		info.setPort(10010);
		int SessionID = 1;

		// create the dummy reader adapter
		DummyReaderPlugin readerAdapter = new DummyReaderPlugin(info);

		try {
			readerAdapter.connect(communicationService.createConnection(
					readerAdapter, info, new DummyProtocol()));
		} catch (RifidiConnectionException e1) {
			Assert.fail();
			e1.printStackTrace();
		} catch (ConnectException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}

		// create the JMS Helper
		JMSHelper jmsHelper = new JMSHelper();
		jmsHelper.initializeJMSQueue(connectionFactory, Integer
				.toString(SessionID));

		// create the reader Session
		IReaderConnection s = new ReaderConnection(info, readerAdapter,
				new DummyProtocol(), SessionID);

		try {
			s.connect();
		} catch (RifidiException e1) {
			e1.printStackTrace();
			Assert.fail();
		}

		try {
			s.startTagStream();
		} catch (RifidiException e1) {
			e1.printStackTrace();
			Assert.fail();
		}

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

			try {
				s.stopTagStream();
			} catch (RifidiException e) {
				e.printStackTrace();
				Assert.fail();
			}

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

	@Inject
	public void setCommunicationService(
			CommunicationService communicationService) {
		this.communicationService = communicationService;
	}
}
