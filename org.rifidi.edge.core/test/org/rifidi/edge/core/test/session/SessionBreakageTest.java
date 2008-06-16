package org.rifidi.edge.core.test.session;


import javax.jms.ConnectionFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.adapter.dummyadapter.DummyConnectionInfo;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderAdapterFactory;
import org.rifidi.edge.core.adapter.dummyadapter.EDummyError;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService;
import org.rifidi.edge.core.session.ISession;
import org.rifidi.edge.core.session.SessionRegistryService;
import org.rifidi.edge.core.session.SessionRegistryServiceImpl;
import org.rifidi.edge.enums.EReaderAdapterState;
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

		SessionRegistryService sessionRegistryService2 = new SessionRegistryServiceImpl();

		DummyConnectionInfo info = new DummyConnectionInfo();
		
		info.setErrorToSet(EDummyError.CONNECT);
		
		ISession s = sessionRegistryService2
				.createReaderSession(info);
		

		s.connect();
		Assert.assertTrue(s.getState() == EReaderAdapterState.ERROR);

		Assert.assertNotNull(s);
	}
	
	@Test
	public void testSessionConnectionErrorHandling2(){
				
		readerAdapterRegistryService.registerReaderAdapter(
				DummyConnectionInfo.class, new DummyReaderAdapterFactory());

		SessionRegistryService sessionRegistryService2 = new SessionRegistryServiceImpl();

		DummyConnectionInfo info = new DummyConnectionInfo();
		
		info.setErrorToSet(EDummyError.CONNECT_RUNTIME);
		
		ISession s = sessionRegistryService2
				.createReaderSession(info);
		

		s.connect();
		Assert.assertTrue(s.getState() == EReaderAdapterState.ERROR);

		Assert.assertNotNull(s);
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
	}

}
