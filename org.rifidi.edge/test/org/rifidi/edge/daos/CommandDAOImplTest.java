/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.daos;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.sensors.AbstractCommandConfiguration;

/**
 * @author myke
 *
 */
public class CommandDAOImplTest {

	private CommandDAOImpl test;

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
		test = new CommandDAOImpl();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		test=null;
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#CommandDAOImpl()}.
	 */
	@Test
	public void testCommandDAOImpl() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#getCommandByID(java.lang.String)}.
	 */
	@Test
	public void testGetCommandByID() {
		String id;
		//TODO: populate the Commands.
		AbstractCommandConfiguration<?> result = getCommandByID(id); 
		assert(result.getID().equals(id));
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#getCommandFactories()}.
	 */
	@Test
	public void testGetCommandFactories() {
		//TODO: Populate the commandFactories
		HashSet<AbstractCommandConfigurationFactory<?>> result = getCommandFactories();
		assert(result.equals(commandFactories));
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#getCommandFactory(java.lang.String)}.
	 */
	@Test
	public void testGetCommandFactory() {
		String id;
		//TODO: populate the Commands.
		AbstractCommandConfigurationFactory<?> result = getCommandFactory(id);
		assert(result.getFactoryID().equals(id));
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#getCommandFactoryByReaderID(java.lang.String)}.
	 */
	@Test
	public void testGetCommandFactoryByReaderID() {
		String id;
		//TODO: populate the Commands.

		Set<AbstractCommandConfigurationFactory<?>> result= getCommandFactoryByReaderID(id);
		for(cmdfact:result){
			assert(cmdfact.getDisplayName().equals(id));
		}
		
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#getCommands()}.
	 */
	@Test
	public void testGetCommands() {
		//TODO populate commands.
		HashSet<AbstractCommandConfiguration<?>> cmds = getCommands();
		assert(cmds.equsla(populatedCommands));
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#bindCommand(org.rifidi.edge.sensors.AbstractCommandConfiguration, java.util.Dictionary)}.
	 */
	@Test
	public void testBindCommand() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#unbindCommand(org.rifidi.edge.sensors.AbstractCommandConfiguration, java.util.Dictionary)}.
	 */
	@Test
	public void testUnbindCommand() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#setCommands(java.util.Set)}.
	 */
	@Test
	public void testSetCommands() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#bindCommandFactory(org.rifidi.edge.configuration.AbstractCommandConfigurationFactory, java.util.Dictionary)}.
	 */
	@Test
	public void testBindCommandFactory() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#unbindCommandFactory(org.rifidi.edge.configuration.AbstractCommandConfigurationFactory, java.util.Dictionary)}.
	 */
	@Test
	public void testUnbindCommandFactory() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#setCommandFactories(java.util.Set)}.
	 */
	@Test
	public void testSetCommandFactories() {
		assert(true);
	}

	/**
	 * Test method for {@link org.rifidi.edge.daos.CommandDAOImpl#setNotifierService(org.rifidi.edge.notification.NotifierService)}.
	 */
	@Test
	public void testSetNotifierService() {
		assert(true);
	}

}
