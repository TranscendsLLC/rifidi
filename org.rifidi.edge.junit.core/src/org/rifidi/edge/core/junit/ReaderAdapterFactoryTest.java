/**
 * 
 */
package org.rifidi.edge.core.junit;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;
import org.rifidi.edge.core.readerPlugin.IReaderPlugin;
import org.rifidi.edge.core.readerPlugin.factory.ReaderPluginFactory;
import org.rifidi.edge.core.service.readerplugin.ReaderPluginRegistryService;
import org.rifidi.edge.jms.service.impl.JMSServiceImpl;
import org.rifidi.edge.readerplugin.dummy.DummyReaderInfo;
import org.rifidi.edge.readerplugin.dummy.DummyReaderPluginFactory;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;


/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class ReaderAdapterFactoryTest {

	ReaderPluginRegistryService readerPluginRegistryService;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ServiceRegistry.getInstance().service(this);
		System.out.println(CommunicationServiceImpl.class.getName());
		System.out.println(JMSServiceImpl.class.getName());
		// might fail.
		readerPluginRegistryService.registerReaderAdapter(
				DummyReaderInfo.class, new DummyReaderPluginFactory());

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		readerPluginRegistryService.unregisterReaderAdapter(DummyReaderInfo.class);
	}

	/**
	 * Test to make sure we can create an adapter from the ConnectionInfo
	 */
	@Test
	public void testCreateReaderAdapter() {
		IReaderPlugin adapter = new ReaderPluginFactory().createReaderAdapter(new DummyReaderInfo());
		Assert.assertNotNull(adapter);
	}

	@Inject
	public void setReaderAdapterRegistryService(
			ReaderPluginRegistryService readerPluginRegistryService) {
		this.readerPluginRegistryService = readerPluginRegistryService;
	}
}
