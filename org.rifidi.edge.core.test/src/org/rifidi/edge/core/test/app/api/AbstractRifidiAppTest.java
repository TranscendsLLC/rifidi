/**
 * 
 */
package org.rifidi.edge.core.test.app.api;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.core.app.api.AbstractRifidiApp;



public class AbstractRifidiAppTest {

	public class AbstractAppTest extends AbstractRifidiApp {
		public AbstractAppTest(String group, String name) {
			super(group, name);
		}
	}
	
	private AbstractAppTest subject;
		
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		this.subject = new AbstractAppTest("group", "name");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#hashCode()}.
	 */
	@Test
	public final void testHashCode() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#AbstractRifidiApp(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testAbstractRifidiApp() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#getName()}.
	 */
	@Test
	public final void testGetName() {
		String appName = "name";
		assertEquals(subject.getName(), appName);
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#getGroup()}.
	 */
	@Test
	public final void testGetGroup() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#getState()}.
	 */
	@Test
	public final void testGetState() {
	//	fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#setEsperService(org.rifidi.edge.core.services.esper.EsperManagementService)}.
	 */
	@Test
	public final void testSetEsperService() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#setAppProperties(java.util.Properties)}.
	 */
	@Test
	public final void testSetAppProperties() {
//		fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#setBundleContext(org.osgi.framework.BundleContext)}.
	 */
	@Test
	public final void testSetBundleContext() {
	//	fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#initialize()}.
	 */
	@Test
	public final void testInitialize() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#lazyStart()}.
	 */
	@Test
	public final void testLazyStart() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#start()}.
	 */
	@Test
	public final void testStart() {
	//	fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#stop()}.
	 */
	@Test
	public final void testStop() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#toString()}.
	 */
	@Test
	public final void testToString() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#equals(java.lang.Object)}.
	 */
	@Test
	public final void testEqualsObject() {
		//fail("Not yet implemented"); // TODO
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.AbstractRifidiApp#compareTo(org.rifidi.edge.core.app.api.RifidiApp)}.
	 */
	@Test
	public final void testCompareTo() {
//		fail("Not yet implemented"); // TODO
	}

}
