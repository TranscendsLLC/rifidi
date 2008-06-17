package org.rifidi.edge.core.test.session;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.TextMessage;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderInfo;
import org.rifidi.edge.core.adapter.dummyadapter.DummyCustomCommand;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderPlugin;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderPluginFactory;
import org.rifidi.edge.core.adapter.dummyadapter.EDummyError;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.readerPlugin.enums.EReaderAdapterState;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class SessionBreakageTest {
	// private static final Log logger =
	// LogFactory.getLog(SessionBreakageTest.class);

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

	@Test
	public void testSessionConnectionErrorHandling() {

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		DummyReaderInfo info = new DummyReaderInfo();

		info.setErrorToSet(EDummyError.CONNECT);

		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();
		Assert.assertTrue(s.getState() == EReaderAdapterState.ERROR);

		Assert.assertNotNull(s);
	}

	@Test
	public void testSessionConnectionErrorHandling2() {

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		DummyReaderInfo info = new DummyReaderInfo();

		info.setErrorToSet(EDummyError.CONNECT_RUNTIME);

		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();
		Assert.assertTrue(s.getState() == EReaderAdapterState.ERROR);

		Assert.assertNotNull(s);
	}

	@Test
	public void testGetTagsError() {

		// set up dummy connection Info
		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(12345);

		info.setErrorToSet(EDummyError.GET_NEXT_TAGS);

		// create the dummy reader adapter
		DummyReaderPlugin readerAdapter = new DummyReaderPlugin(info);

		try {
			readerAdapter.connect();
		} catch (RifidiConnectionException e1) {
			// TODO Auto-generated catch block
			Assert.fail();
			e1.printStackTrace();
		}

		// create the reader Session
		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();

		s.startTagStream();

		Connection c;
		try {
			c = connectionFactory.createConnection();
			c.start();

			javax.jms.Session jmsSession = c.createSession(false,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
			Destination dest = jmsSession.createQueue(Integer.toString(s
					.getSessionID()));
			MessageConsumer consumer = jmsSession.createConsumer(dest);

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
		} catch (NullPointerException e) {
			Assert.assertSame(EReaderAdapterState.ERROR, s.getState());
			Assert.assertNotNull(s.getErrorCause());
			Assert
					.assertTrue(s.getErrorCause() instanceof RifidiConnectionIllegalStateException);
		}

	}

	@Test
	public void testGetTagsErrorRuntime() {

		// set up dummy connection Info
		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(12345);

		info.setErrorToSet(EDummyError.GET_NEXT_TAGS_RUNTIME);

		// create the dummy reader adapter
		DummyReaderPlugin readerAdapter = new DummyReaderPlugin(info);

		try {
			readerAdapter.connect();
		} catch (RifidiConnectionException e1) {
			// TODO Auto-generated catch block
			Assert.fail();
			e1.printStackTrace();
		}

		// create the reader Session
		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();

		s.startTagStream();

		Connection c;
		try {
			c = connectionFactory.createConnection();
			c.start();

			javax.jms.Session jmsSession = c.createSession(false,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
			Destination dest = jmsSession.createQueue(Integer.toString(s
					.getSessionID()));
			MessageConsumer consumer = jmsSession.createConsumer(dest);

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
		} catch (NullPointerException e) {

			Assert.assertSame(EReaderAdapterState.ERROR, s.getState());
			Assert.assertNotNull(s.getErrorCause());
			Assert.assertTrue(s.getErrorCause() instanceof RuntimeException);
		}

	}

	@Test
	public void testSessionSendCustomCommand() {

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		DummyReaderInfo info = new DummyReaderInfo();

		info.setErrorToSet(EDummyError.SEND_CUSTOM_COMMAND);

		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();

		s.sendCustomCommand(new DummyCustomCommand("Command"));

		System.out.println(s.getState());

		Assert.assertSame(s.getState(), EReaderAdapterState.ERROR);
		Assert
				.assertTrue(s.getErrorCause() instanceof RifidiConnectionIllegalStateException);
	}

	@Test
	public void testSessionSendCustomCommandRealTime() {

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		DummyReaderInfo info = new DummyReaderInfo();

		info.setErrorToSet(EDummyError.SEND_CUSTOM_COMMAND_RUNTIME);

		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();

		s.sendCustomCommand(new DummyCustomCommand("Command"));

		System.out.println(s.getState());

		Assert.assertSame(s.getState(), EReaderAdapterState.ERROR);
		Assert.assertTrue(s.getErrorCause() instanceof RuntimeException);
	}

	@Test
	public void testSessionDisconnectCommand() {

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		DummyReaderInfo info = new DummyReaderInfo();

		info.setErrorToSet(EDummyError.DISCONNECT);

		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();

		s.disconnect();

		Assert.assertSame(s.getState(), EReaderAdapterState.ERROR);
		Assert
				.assertTrue(s.getErrorCause() instanceof RifidiConnectionException);
	}

	@Test
	public void testSessionDisconnectRealTime() {

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		DummyReaderInfo info = new DummyReaderInfo();

		info.setErrorToSet(EDummyError.DISCONNECT_RUNTIME);

		IReaderConnection s = sessionRegistryService
				.createReaderConnection(info);

		s.connect();

		s.disconnect();

		Assert.assertSame(s.getState(), EReaderAdapterState.ERROR);
		Assert.assertTrue(s.getErrorCause() instanceof RuntimeException);
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
