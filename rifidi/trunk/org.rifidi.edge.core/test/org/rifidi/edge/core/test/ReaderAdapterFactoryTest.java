/**
 * 
 */
package org.rifidi.edge.core.test;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.adapter.dummyadapter.DummyConnectionInfo;
import org.rifidi.edge.core.adapter.dummyadapter.DummyReaderAdapterFactory;
import org.rifidi.edge.core.readerAdapter.IReaderAdapter;
import org.rifidi.edge.core.readerAdapter.ReaderAdapterFactory;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;


/**
 * @author Matthew Dean - matt@pramari.com
 * 
 */
public class ReaderAdapterFactoryTest {

	ReaderAdapterRegistryService readerAdapterRegistryService;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		// TODO finish this junit
		ServiceRegistry.getInstance().service(this);
		// might fail.
		readerAdapterRegistryService.registerReaderAdapter(
				DummyConnectionInfo.class, new DummyReaderAdapterFactory());

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// TODO finish this junit
		readerAdapterRegistryService.unregisterReaderAdapter(DummyConnectionInfo.class);
	}

	/**
	 * Test to make sure we can create an adapter from the ConnectionInfo
	 */
	@Test
	public void testCreateReaderAdapter() {
		IReaderAdapter adapter = ReaderAdapterFactory.getInstance()
				.createReaderAdapter(new DummyConnectionInfo());
		Assert.assertNotNull(adapter);
	}

	@Inject
	public void setReaderAdapterRegistryService(
			ReaderAdapterRegistryService readerAdapterRegistryService) {
		this.readerAdapterRegistryService = readerAdapterRegistryService;
	}
}
