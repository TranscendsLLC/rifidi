package org.rifidi.edge.core.test.session;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.adapter.dummyadapter.DummyConnectionInfo;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderAdapterFactory;
import org.rifidi.edge.core.connection.ReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryServiceImpl;
import org.rifidi.edge.core.readerPluginService.ReaderAdapterRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class SessionRegistryTest {

	private ReaderConnectionRegistryService sessionRegistryService;

	private ReaderAdapterRegistryService readerAdapterRegistryService;

	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);

	}

	/**
	 * Test to make sure SessionRegistry is available as OSGi Service
	 */
	@Test
	public void testGetSessionRegistry() {
		Assert.assertNotNull(sessionRegistryService);
		Assert.assertTrue(sessionRegistryService.sessionCount() == 0);
	}

	/**
	 * Test to make sure ReaderAdapterRegistry is available as OSGi Service
	 */
	@Test
	public void testGetReaderAdapterRegistryService() {
		Assert.assertNotNull(readerAdapterRegistryService);
	}

	/**
	 * Test to make sure we can create a new Session
	 */
	@Test
	public void testCreateSession() {
		readerAdapterRegistryService.registerReaderAdapter(
				DummyConnectionInfo.class, new DummyReaderAdapterFactory());

		ReaderConnectionRegistryService sessionRegistryService2 = new ReaderConnectionRegistryServiceImpl();

		ReaderConnection s = sessionRegistryService2
				.createReaderSession(new DummyConnectionInfo());

		Assert.assertNotNull(s);
	}

	@Test
	public void testSetAndGetSession() {
		ReaderConnectionRegistryService sessionRegistryService2 = new ReaderConnectionRegistryServiceImpl();
		
		ReaderConnection s = sessionRegistryService2
				.createReaderSession(new DummyConnectionInfo());

		Assert.assertTrue(sessionRegistryService2.getReaderSession(s.getSessionID()) == s);
	}

	@Inject
	public void setSessionRegistryService(ReaderConnectionRegistryService regSer) {
		this.sessionRegistryService = regSer;
	}

	@Inject
	public void setAdapterRegistryService(
			ReaderAdapterRegistryService readerAdapterRegistryService) {
		this.readerAdapterRegistryService = readerAdapterRegistryService;
	}

}
