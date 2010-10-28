/**
 * 
 */
package org.rifidi.edge.core.test.app.api;

import static org.junit.Assert.*;

import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.core.app.api.service.EsperUtil;

/**
 * @author manoj
 *
 */
public class EsperUtilTest {

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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.EsperUtil#timeUnitToEsperTime(float, java.util.concurrent.TimeUnit)}.
	 */
	
	@Test
	public void testTimeUnitToEsperTime() {		
		assertEquals("50.0 sec", EsperUtil.timeUnitToEsperTime(50, TimeUnit.SECONDS));
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.EsperUtil#esperTimetoTime(java.lang.String)}.
	 */
	@Test
	public void testEsperTimetoTime() {
		Float f = new Float(50.0);
		assertEquals(f, EsperUtil.esperTimetoTime("50"));
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.EsperUtil#esperTimetoTimeUnit(java.lang.String)}.
	 */
	@Test
	public void testEsperTimetoTimeUnit() {
		assertEquals(TimeUnit.SECONDS, EsperUtil.esperTimetoTimeUnit("1.5 sec"));
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.EsperUtil#buildInsertStatement(java.lang.String, java.util.List)}.
	 */
	@Test
	public void testBuildInsertStatementStringListOfReadZone() {
		
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.EsperUtil#buildInsertStatement(java.lang.String, org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone)}.
	 */
	@Test
	public void testBuildInsertStatementStringReadZone() {
		fail("Not yet implemented");
	}

}
