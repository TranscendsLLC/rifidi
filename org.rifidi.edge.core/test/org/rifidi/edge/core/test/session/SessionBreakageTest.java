package org.rifidi.edge.core.test.session;


import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
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
import org.rifidi.edge.core.adapter.dummyadapter.DummyCustomCommand;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderAdapter;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderAdapterFactory;
import org.rifidi.edge.core.adapter.dummyadapter.EDummyError;
import org.rifidi.edge.core.exception.RifidiException;
import org.rifidi.edge.core.exception.adapter.RifidiConnectionException;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService;
import org.rifidi.edge.core.readerAdapterenums.EReaderAdapterState;
import org.rifidi.edge.core.session.Session;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.core.session.SessionRegistryServiceImpl;

import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class SessionBreakageTest {
	private static final Log logger = LogFactory.getLog(SessionBreakageTest.class);

	private ConnectionFactory connectionFactory;
	
	private SessionRegistryService sessionRegistryService;

	private ReaderAdapterRegistryService readerAdapterRegistryService;

	
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
	public void testSessionConnectionErrorHandling(){
				
		readerAdapterRegistryService.registerReaderAdapter(
				DummyConnectionInfo.class, new DummyReaderAdapterFactory());

		DummyConnectionInfo info = new DummyConnectionInfo();
		
		info.setErrorToSet(EDummyError.CONNECT);
		
		Session s = sessionRegistryService
				.createReaderSession(info);
		

		s.connect();
		Assert.assertTrue(s.getState() == EReaderAdapterState.ERROR);

		Assert.assertNotNull(s);
	}
	
	@Test
	public void testSessionConnectionErrorHandling2(){
				
		readerAdapterRegistryService.registerReaderAdapter(
				DummyConnectionInfo.class, new DummyReaderAdapterFactory());


		DummyConnectionInfo info = new DummyConnectionInfo();
		
		info.setErrorToSet(EDummyError.CONNECT_RUNTIME);
		
		Session s = sessionRegistryService
				.createReaderSession(info);
		

		s.connect();
		Assert.assertTrue(s.getState() == EReaderAdapterState.ERROR);

		Assert.assertNotNull(s);
	}
	
	
	@Test
	public void testGetTagsError(){

		// set up dummy connection Info
		DummyConnectionInfo info = new DummyConnectionInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(12345);

		info.setErrorToSet(EDummyError.GET_NEXT_TAGS);
		
		// create the dummy reader adapter
		DummyReaderAdapter readerAdapter = new DummyReaderAdapter(info);
		
		try {
			readerAdapter.connect();
		} catch (RifidiConnectionException e1) {
			// TODO Auto-generated catch block
			Assert.fail();
			e1.printStackTrace();
		}

		// create the reader Session
		Session s = sessionRegistryService.createReaderSession(info);

		s.connect();
		
		s.startTagStream();

		Connection c;
		try {
			c = connectionFactory.createConnection();
			c.start();

			javax.jms.Session jmsSession = c.createSession(false,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
			Destination dest = jmsSession.createQueue(Integer.toString(s.getSessionID()));
			MessageConsumer consumer = jmsSession.createConsumer(dest);

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
		} catch (NullPointerException e){
			Assert.assertSame(EReaderAdapterState.ERROR, s.getState());
			Assert.assertNotNull(s.getErrorCause());
			Assert.assertTrue(s.getErrorCause() instanceof RifidiException);
		}

	}
	
	@Test
	public void testGetTagsErrorRuntime(){

		// set up dummy connection Info
		DummyConnectionInfo info = new DummyConnectionInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(12345);

		info.setErrorToSet(EDummyError.GET_NEXT_TAGS_RUNTIME);
		
		// create the dummy reader adapter
		DummyReaderAdapter readerAdapter = new DummyReaderAdapter(info);
		
		try {
			readerAdapter.connect();
		} catch (RifidiConnectionException e1) {
			// TODO Auto-generated catch block
			Assert.fail();
			e1.printStackTrace();
		}

		// create the reader Session
		Session s = sessionRegistryService.createReaderSession(info);

		s.connect();
		
		s.startTagStream();

		Connection c;
		try {
			c = connectionFactory.createConnection();
			c.start();

			javax.jms.Session jmsSession = c.createSession(false,
					javax.jms.Session.AUTO_ACKNOWLEDGE);
			Destination dest = jmsSession.createQueue(Integer.toString(s.getSessionID()));
			MessageConsumer consumer = jmsSession.createConsumer(dest);

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
		} catch (NullPointerException e){
			
			Assert.assertSame(EReaderAdapterState.ERROR, s.getState());
			Assert.assertNotNull(s.getErrorCause());
			Assert.assertTrue(s.getErrorCause() instanceof RuntimeException);
		}

	}
	
	@Test
	public void testSessionSendCustomCommand(){
				
		readerAdapterRegistryService.registerReaderAdapter(
				DummyConnectionInfo.class, new DummyReaderAdapterFactory());

		

		DummyConnectionInfo info = new DummyConnectionInfo();
		
		info.setErrorToSet(EDummyError.SEND_CUSTOM_COMMAND);
		
		Session s = sessionRegistryService
				.createReaderSession(info);
		

		s.connect();


		s.sendCustomCommand(new DummyCustomCommand("Command"));
		
		System.out.println(s.getState());
		
		Assert.assertTrue(s.getState() == EReaderAdapterState.ERROR);
	}
	
	
	
	@Inject
	public void setConnectionFactory(ConnectionFactory connectionFactory) {
		this.connectionFactory = connectionFactory;
	}

	@Inject
	public void setSessionRegistryService(SessionRegistryService regSer) {
		this.sessionRegistryService = regSer;
	}

	@Inject
	public void setAdapterRegistryService(
			ReaderAdapterRegistryService readerAdapterRegistryService) {
		this.readerAdapterRegistryService = readerAdapterRegistryService;
		
		readerAdapterRegistryService.registerReaderAdapter(DummyConnectionInfo.class, new DummyReaderAdapterFactory());
	}

}
