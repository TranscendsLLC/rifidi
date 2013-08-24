/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.console;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.configuration.ConfigurationService;
import org.rifidi.edge.daos.CommandDAO;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.services.LoggingService;

/**
 * @author myke
 *
 */
public class RifidiEdgeServerCommandsTest {
	
	
	private RifidiEdgeServerCommands resc;
	private CommandInterpreter ci;
	
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
	public void setUp() throws Exception{
		resc = new RifidiEdgeServerCommands();
		//TODO: default instantiate the above, possibly using EasyMock.
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		ci = null;
		resc = null;
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#setConfigService(org.rifidi.edge.configuration.ConfigurationService)}.
	 */
	@Test
	public void testSetConfigService() {
		setConfigService(new ConfigService());
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#setCommandDAO(org.rifidi.edge.daos.CommandDAO)}.
	 */
	@Test
	public void testSetCommandDAO() {
		setCommandDAO(new CommandDAO());
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#setReaderDAO(org.rifidi.edge.daos.ReaderDAO)}.
	 */
	@Test
	public void testSetReaderDAO() {
		setReaderDAO(new ReaderDAO());
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#setLoggingService(org.rifidi.edge.services.LoggingService)}.
	 */
	@Test
	public void testSetLoggingService() {
		setLoggingService(new LoggingService());
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_readertypes(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_readertypes() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_readers(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_readers() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_configurations(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_configurations() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_createreader(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_createreader() {
		//no strings
		Integer assertAgainst = _createreader(ci); 
		assert(assertAgainst==Integer(1));
		//one bogus string
		assertAgainst = _createreader(ci);
		assert(assertAgainst==Integer(2));
		//one valid strings
		assertAgainst = _createreader(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_deletereader(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_deletereader() {
		CommandInterpreter ci;
		//One valid string.
		_createreader(ci);
		//no strings
		Integer assertAgainst = _deletereader(ci);
		assert(assertAgainst==Integer(1));
		//one invalid string.
		assertAgainst = _deletereader(ci);
		assert(assertAgainst==Integer(2));
		//one valid string.
		assertAgainst = _deletereader(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_getproperties(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_getproperties() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst =_getproperties(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst =_getproperties(ci);
		assert(assertAgainst==Integer(2));
		//valid
		assertAgainst =_getproperties(ci);
		assert(assertAgainst==null);

	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_setproperties(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_setproperties() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _setproperties(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst = _setproperties(ci);
		assert(assertAgainst==Integer(2));
		//valid
		assertAgainst = _setproperties(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_applypropchanges(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_applypropchanges() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _applypropchanges(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst = _applypropchanges(ci);
		assert(assertAgainst==Integer(2));
		//valid
		assertAgainst = _applypropchanges(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_commands(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_commands() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_commandtypes(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_commandtypes() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_createcommand(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_createcommand() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _createcommand(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst = _createcommand(ci);
		assert(assertAgainst==Integer(2));
		//valid
		assertAgainst = _createcommand(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_deletecommand(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_deletecommand() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _deletecommand(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst = _deletecommand(ci);
		assert(assertAgainst==Integer(2));
		//valid
		assertAgainst = _deletecommand(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_createsession(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_createsession() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _createsession(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst = _createsession(ci);
		assert(assertAgainst==Integer(2));
		//valid
		assertAgainst = _createsession(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_deletesession(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_deletesession() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _createsession(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst = _createsession(ci);
		assert(assertAgainst==Integer(2));
		//two random 
		assertAgainst = _createsession(ci);
		assert(assertAgainst==Integer(3));
		//two valid
		assertAgainst = _createsession(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_startsession(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_startsession() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _startsession(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst = _startsession(ci);
		assert(assertAgainst==Integer(2));
		//two random 
		assertAgainst = _startsession(ci);
		assert(assertAgainst==Integer(3));
		//Valid reader, invalid ID (Nonexistent)
		Integer assertAgainst = _startsession(ci);
		assert(assertAgainst==Integer(4));
		//Valid reader, Invalid ID (Number format exception)
		assertAgainst = _startsession(ci);
		assert(assertAgainst==Integer(5));
		//two valid
		assertAgainst = _startsession(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_stopsession(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_stopsession() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _stopsession(ci);
		assert(assertAgainst==Integer(1));
		//random
		assertAgainst = _stopsession(ci);
		assert(assertAgainst==Integer(2));
		//two random 
		assertAgainst = _stopsession(ci);
		assert(assertAgainst==Integer(3));
		//Valid reader, invalid ID (Nonexistent)
		Integer assertAgainst = _stopsession(ci);
		assert(assertAgainst==Integer(4));
		//Valid reader, Invalid ID (Number format exception)
		assertAgainst = _stopsession(ci);
		assert(assertAgainst==Integer(5));
		//two valid
		assertAgainst = _stopsession(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_executecommand(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_executecommand() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _executecommand(ci);
		assert(assertAgainst==Integer(1));
		//random
		Integer assertAgainst = _executecommand(ci);
		assert(assertAgainst==Integer(1));
		//2 random
		Integer assertAgainst = _executecommand(ci);
		assert(assertAgainst==Integer(1));
		//3 random
		Integer assertAgainst = _executecommand(ci);
		assert(assertAgainst==Integer(1));
		//4 random
		assertAgainst = _executecommand(ci);
		assert(assertAgainst==Integer(2));
		//valid 1, invalid 3
		assertAgainst = _executecommand(ci);
		assert(assertAgainst==Integer(3));
		//Valid 2, invalid 2
		Integer assertAgainst = _executecommand(ci);
		assert(assertAgainst==Integer(4));
		//Valid 3, Invalid interval (Number format exception)
		assertAgainst = _executecommand(ci);
		assert(assertAgainst==Integer(5));
		//4 valid
		assertAgainst = _executecommand(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_killcommand(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_killcommand() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _killcommand(ci);
		assert(assertAgainst==Integer(1));
		//1 random
		assertAgainst = _killcommand(ci);
		assert(assertAgainst==Integer(1));
		//2 random (NFE)
		assertAgainst = _killcommand(ci);
		assert(assertAgainst==Integer(2));
		//2 valid
		assertAgainst = _killcommand(ci);
		assert(assertAgainst==null);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_save(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_save() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#_setloglevel(org.eclipse.osgi.framework.console.CommandInterpreter)}.
	 */
	@Test
	public void test_setloglevel() {
		CommandInterpreter ci;
		//empty
		Integer assertAgainst = _setloglevel(ci);
		assert(assertAgainst==Integer(1));
		//1 random
		assertAgainst = _setloglevel(ci);
		assert(assertAgainst==Integer(1));
		//2 random (NFE)
		assertAgainst = _setloglevel(ci);
		assert(assertAgainst==Integer(2));
		//2 valid
		assertAgainst = _setloglevel(ci);
		assert(assertAgainst==null);	}

	/**
	 * Test method for {@link org.rifidi.edge.console.RifidiEdgeServerCommands#getHelp()}.
	 */
	@Test
	public void testGetHelp() {
		assert(true);
	}

}
