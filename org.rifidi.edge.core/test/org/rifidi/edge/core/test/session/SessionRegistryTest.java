package org.rifidi.edge.core.test.session;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.adapter.dummyadapter.DummyConnectionInfo;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderAdapterFactory;
import org.rifidi.edge.core.connection.ReaderConnection;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.connection.ReaderConnectionRegistryServiceImpl;
import org.rifidi.edge.core.readerPluginService.ReaderPluginRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

public class SessionRegistryTest {

	private ReaderConnectionRegistryService sessionRegistryService;

	private ReaderPluginRegistryService readerPluginRegistryService;

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
		// TODO Jerry why do we need the session count here? Isn't it sufficient to ask if it is null or something like that?
		//Assert.assertTrue(sessionRegistryService.sessionCount() == 0);
		Assert.fail("TODO");
	}

	/**
	 * Test to make sure ReaderAdapterRegistry is available as OSGi Service
	 */
	@Test
	public void testGetReaderAdapterRegistryService() {
		Assert.assertNotNull(readerPluginRegistryService);
	}

	/**
	 * Test to make sure we can create a new Session
	 */
	@Test
	public void testCreateSession() {
		readerPluginRegistryService.registerReaderAdapter(
				DummyConnectionInfo.class, new DummyReaderAdapterFactory());

		ReaderConnectionRegistryService sessionRegistryService2 = new ReaderConnectionRegistryServiceImpl();

		ReaderConnection s = sessionRegistryService2
				.createReaderConnection(new DummyConnectionInfo());

		Assert.assertNotNull(s);
	}

	@Test
	public void testSetAndGetSession() {
		ReaderConnectionRegistryService sessionRegistryService2 = new ReaderConnectionRegistryServiceImpl();
		
		ReaderConnection s = sessionRegistryService2
				.createReaderConnection(new DummyConnectionInfo());

		Assert.assertTrue(sessionRegistryService2.getReaderConnection(s.getSessionID()) == s);
	}

	@Inject
	public void setSessionRegistryService(ReaderConnectionRegistryService regSer) {
		this.sessionRegistryService = regSer;
	}

	@Inject
	public void setAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;
	}

}
