/**
 * 
 */
package org.rifidi.edge.core.test.app.api.service.tagmonitor.impl;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.core.app.api.service.tagmonitor.impl.ReadZoneMonitorEsperFactory;

/**
 * @author manoj
 *
 */
public class ReadZoneMonitorEsperFactoryTest {
	ReadZone readZone;
	ReadZone readZone1;
	List<ReadZone> readZones;
	Integer windowID;
	Float departureWaitTime;
	TimeUnit timeUnit;
	ReadZoneMonitorEsperFactory readZoneMonitorEsperFactory;
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
		readZone = new ReadZone("readerID");
		readZone1 =new ReadZone("readerID1");
		readZones = new ArrayList<ReadZone>();
		readZones.add(readZone);
		readZones.add(readZone1);
		windowID = new Integer(5);
		departureWaitTime = new Float(10);
		timeUnit = TimeUnit.SECONDS;
		readZoneMonitorEsperFactory = new ReadZoneMonitorEsperFactory(readZones, windowID, departureWaitTime, timeUnit);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

		
	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.impl.ReadZoneMonitorEsperFactory#createStatements()}.
	 */
	@Test
	public void testCreateStatements() {
		/**List<String> stmt = new ArrayList<String>();
		stmt = readZoneMonitorEsperFactory.createStatements();
		List expStmt  = new ArrayList<String>();
		
		
		List<ReadZone> expReadZones = new ArrayList<ReadZone>();
		expReadZones.add(readZone);
		expReadZones.add(readZone1);
		
		expStmt.add(expReadZones);
		String expWindowStmt = new String("create window tags_5.std:firstunique(tag.Id, readerId, antennaID) as TagReadEvent");
		expStmt.add(expWindowStmt);
		*/
		
	}

	/**
	 * Test method for {@link org.rifidi.edge.core.app.api.service.tagmonitor.impl.ReadZoneMonitorEsperFactory#createQuery()}.
	 */
	@Test
	public void testCreateQuery() {
		assertEquals("select irstream * from tags_5", readZoneMonitorEsperFactory.createQuery());
	}

}
