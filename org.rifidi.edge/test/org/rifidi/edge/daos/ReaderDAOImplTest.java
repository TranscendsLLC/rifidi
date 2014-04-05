/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.daos;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.AbstractSensorFactory;

/**
 * @author myke
 *
 */
public class ReaderDAOImplTest {

	private ReaderDAOImpl read;
	
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
		read = new ReaderDAOImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		read = null;
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#ReaderDAOImpl()}.
	 */
	@Test
	public void testReaderDAOImpl() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#getReaderByID(java.lang.String)}.
	 */
	@Test
	public void testGetReaderByID() {
		String id;
		AbstractSensor<?> sensor = getReaderByID(id);
		assert(sensor.getID().equals(id));
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#getReaderFactories()}.
	 */
	@Test
	public void testGetReaderFactories() {
		//TODO: Populate readerFactories.
		HashSet<AbstractSensorFactory<?>> result = GetReaderFactories();
		assert(result.equals(testReaders));
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#getReaderFactoryByID(java.lang.String)}.
	 */
	@Test
	public void testGetReaderFactoryByID() {
		String id;
		//TODO populate readerfactories.
		AbstractSensorFactory<?> result = getReaderFactoryByID(id);
		assert(result.getFactoryID().equals(id));
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#getReaders()}.
	 */
	@Test
	public void testGetReaders() {
		//TODO Populate readers.
		HashSet<AbstractSensor<?>> result = getReaders();
		for(reader : result){
			assert(reader.getID().equals(testReaders));
		}
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#bindReaderFactory(org.rifidi.edge.sensors.AbstractSensorFactory, java.util.Dictionary)}.
	 */
	@Test
	public void testBindReaderFactory() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#unbindReaderFactory(org.rifidi.edge.sensors.AbstractSensorFactory, java.util.Dictionary)}.
	 */
	@Test
	public void testUnbindReaderFactory() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#setReaderFactories(java.util.Set)}.
	 */
	@Test
	public void testSetReaderFactories() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#bindReader(org.rifidi.edge.sensors.AbstractSensor, java.util.Dictionary)}.
	 */
	@Test
	public void testBindReader() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#unbindReader(org.rifidi.edge.sensors.AbstractSensor, java.util.Dictionary)}.
	 */
	@Test
	public void testUnbindReader() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#setReader(java.util.Set)}.
	 */
	@Test
	public void testSetReader() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.ReaderDAOImpl#setNotifierService(org.rifidi.edge.notification.NotifierService)}.
	 */
	@Test
	public void testSetNotifierService() {
		assert(true);
	}

}