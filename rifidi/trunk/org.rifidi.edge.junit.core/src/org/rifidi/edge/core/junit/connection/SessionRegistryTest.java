package org.rifidi.edge.core.junit.connection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;
import org.rifidi.edge.core.connection.IReaderConnection;
import org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService;
import org.rifidi.edge.core.service.readerconnection.impl.ReaderConnectionRegistryServiceImpl;
import org.rifidi.edge.core.service.readerplugin.ReaderPluginRegistryService;
import org.rifidi.edge.jms.service.impl.JMSServiceImpl;
import org.rifidi.edge.readerplugin.dummy.DummyReaderInfo;
import org.rifidi.edge.readerplugin.dummy.DummyReaderPluginFactory;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author Jerry Maine - jerry@pramari.com
 * 
 */
public class SessionRegistryTest {

	private ReaderConnectionRegistryService sessionRegistryService;

	private ReaderPluginRegistryService readerPluginRegistryService;

	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);

		System.out.println(CommunicationServiceImpl.class.getName());
		System.out.println(JMSServiceImpl.class.getName());
	}

	/**
	 * Test to make sure SessionRegistry is available as OSGi Service
	 */
	@Test
	public void testGetSessionRegistry() {
		Assert.assertNotNull(sessionRegistryService);
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
				DummyReaderInfo.class, new DummyReaderPluginFactory());

		ReaderConnectionRegistryService sessionRegistryService2 = new ReaderConnectionRegistryServiceImpl();

		IReaderConnection s = sessionRegistryService2
				.createReaderConnection(new DummyReaderInfo());

		Assert.assertNotNull(s);
	}

	@Test
	public void testSetAndGetSession() {
		ReaderConnectionRegistryService sessionRegistryService2 = new ReaderConnectionRegistryServiceImpl();

		IReaderConnection s = sessionRegistryService2
				.createReaderConnection(new DummyReaderInfo());

		Assert.assertTrue(sessionRegistryService2.getReaderConnection(s
				.getSessionID()) == s);
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
