/**
 * 
 */
package org.rifidi.edge.core.test.app.api.service.tagmonitor;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;

/**
 * @author manoj
 *
 */
public class ReadZoneTest {
	ReadZone rZone;
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		rZone = new ReadZone("readerID");
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone#ReadZone(java.lang.String)}.
	 */
	@Test
	public void testReadZoneString() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone#ReadZone(java.lang.String, java.lang.Integer[])}.
	 */
	@Test
	public void testReadZoneStringIntegerArray() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone#ReadZone(java.lang.String, java.util.List, boolean, java.lang.Integer[])}.
	 */
	@Test
	public void testReadZoneStringListOfStringBooleanIntegerArray() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone#createReadZone(java.util.Properties)}.
	 */
	@Test
	public void testCreateReadZone() {
		//fail("Not yet implemented");
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone#getReaderID()}.
	 */
	@Test
	public void testGetReaderID() {
		
		rZone.setReaderID("A001");
		assertEquals("A001", rZone.getReaderID());
		
	}
	
	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone#isInclude()}.
	 */
	@Test
	public void testIsInclude() {
	//	fail("Not yet implemented");
	}

	
	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone#setInclude(boolean)}.
	 */
	@Test
	public void testSetInclude() {
		
		rZone.setInclude(true);
		assertEquals(true, rZone.isInclude());
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone#clone()}.
	 */
	@Test
	public void testClone() {
		//fail("Not yet implemented");
	}

}
