package org.rifidi.edge.core.junit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.connection.impl.ReaderConnection;
import org.rifidi.edge.core.service.readerconnection.ReaderConnectionRegistryService;
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
public class RegistryAutoRemoveTest {
	// private static final Log logger =
	// LogFactory.getLog(RegistryAutoRemoveTest.class);

	private ReaderConnectionRegistryService connectionRegistryService;

	private ReaderPluginRegistryService readerPluginRegistryService;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// System.out.println(CommunicationServiceImpl.class.getName());
		System.out.println(JMSServiceImpl.class.getName());
		ServiceRegistry.getInstance().service(this);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRegistryAutoRemove() {
		Assert.assertNotNull(connectionRegistryService);
		Assert.assertNotNull(readerPluginRegistryService);
		Assert.assertNotNull(connectionRegistryService
				.getAllReaderConnections());

		DummyReaderInfo info = new DummyReaderInfo();
		info.setIPAddress("127.0.0.1");
		info.setPort(10001);
		connectionRegistryService.createReaderConnection(info);

		DummyReaderInfo info2 = new DummyReaderInfo();
		info2.setIPAddress("127.0.0.1");
		info2.setPort(10002);
		connectionRegistryService.createReaderConnection(info2);

		readerPluginRegistryService
				.unregisterReaderAdapter(DummyReaderInfo.class);

		for (ReaderConnection c : connectionRegistryService
				.getAllReaderConnections()) {
			System.out.println(c.getAdapter());
		}

		Assert.assertTrue(connectionRegistryService.getAllReaderConnections()
				.isEmpty());

	}

	@Inject
	public void setConnectionRegistryService(
			ReaderConnectionRegistryService regSer) {
		this.connectionRegistryService = regSer;
	}

	@Inject
	public void setAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;

		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());
	}

}
