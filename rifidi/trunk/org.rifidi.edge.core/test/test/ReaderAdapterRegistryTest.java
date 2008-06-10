/**
 * 
 */
package test;


import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.readerAdapter.ISpecificReaderAdapterFactory;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryService;
import org.rifidi.edge.core.readerAdapterService.ReaderAdapterRegistryServiceImpl;

/**
 * @author Matthew Dean - matt@pramari.com
 *
 */
public class ReaderAdapterRegistryTest {

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests connecting the reader
	 */
	@Test
	public void testRegisterReaderAdapter() {
		Assert.fail();
	}
	
	/**
	 * Tests connecting the reader
	 */
	@Test
	public void testRegisterListener() {
		Assert.fail();
	}
	
	/**
	 * Tests connecting the reader
	 */
	@Test
	public void testUnRegisterListener() {
		Assert.fail();
		ReaderAdapterRegistryService registry = new ReaderAdapterRegistryServiceImpl();
		
		ISpecificReaderAdapterFactory factory = new DummyReaderAdapterFactory();
		registry.registerReaderAdapter(DummyConnectionInfo.class, factory);
		
		/*registry.getSpecReaderAdapterFactory() */

	}
}
