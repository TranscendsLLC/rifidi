/*
 *  AlienReaderAdapterTest.java
 *
 *  Created:	Jun 20, 2006
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.readerplugin.alien.junit;

import java.io.IOException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.communication.buffer.ConnectionBuffer;
import org.rifidi.edge.core.communication.service.CommunicationService;
import org.rifidi.edge.core.communication.service.impl.CommunicationServiceImpl;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.edge.readerPlugin.alien.AlienProtocol;
import org.rifidi.edge.readerPlugin.alien.AlienReaderInfo;
import org.rifidi.edge.readerPlugin.alien.AlienReaderPlugin;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * Test class for the plugin of the Alien reader.
 * 
 * @author Matt
 */
public class AlienReaderPluginTest {

	private CommunicationService commSer;
	
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		commSer = new CommunicationServiceImpl();
		ServiceRegistry.getInstance().service(this);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Tests connecting the reader
	 * 
	 * @throws RifidiConnectionIllegalStateException
	 */
	@Test
	public void testConnect() {
		if (commSer == null) {
			System.out.println("commSer is null");
		}
		AlienReaderInfo connectionInfo = new AlienReaderInfo();
		connectionInfo.setIPAddress("192.168.1.100");
		connectionInfo.setPort(23);
		connectionInfo.setUsername("alien");
		connectionInfo.setPassword("password");

		AlienReaderPlugin alienReaderAdapter = new AlienReaderPlugin(
				connectionInfo);

		try {
			ConnectionBuffer omg = commSer.createConnection(alienReaderAdapter,
					connectionInfo, new AlienProtocol());
			alienReaderAdapter.connect(omg);
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Tests the tag streaming portion of the code
	 */
	@Test
	public void testStream() {

		AlienReaderInfo connectionInfo = new AlienReaderInfo();
		connectionInfo.setIPAddress("192.168.1.100");
		connectionInfo.setPort(23);
		connectionInfo.setUsername("alien");
		connectionInfo.setPassword("password");

		AlienReaderPlugin alienReaderAdapter = new AlienReaderPlugin(
				connectionInfo);

		if (alienReaderAdapter == null)
			System.out.println("the readeradapter is null");

		try {
			ConnectionBuffer omg = commSer.createConnection(alienReaderAdapter,
					connectionInfo, new AlienProtocol());
			alienReaderAdapter.connect(omg);
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < 5; i++) {
			List<TagRead> tagList = null;
			try {
				tagList = alienReaderAdapter.getNextTags();
			} catch (RifidiConnectionIllegalStateException e) {
				e.printStackTrace();
				Assert.fail();
			}
			System.out.println("taglist size: " + tagList.size());
			for (TagRead t : tagList) {
				System.out.println(ByteAndHexConvertingUtility.toHexString(t
						.getId()));
			}
		}
	}

	/**
	 * Tests the raw command sending to the reader
	 */
	// @Test
	// public void testRawCommand() {
	// Assert.fail();
	// }
	/**
	 * Tests disconnecting the reader
	 */
	@Test
	public void testDisconnect() {
		AlienReaderInfo connectionInfo = new AlienReaderInfo();
		connectionInfo.setIPAddress("192.168.1.100");
		connectionInfo.setPort(23);
		connectionInfo.setUsername("alien");
		connectionInfo.setPassword("password");

		AlienReaderPlugin alienReaderAdapter = new AlienReaderPlugin(
				connectionInfo);
		try {
			ConnectionBuffer omg = commSer.createConnection(alienReaderAdapter,
					connectionInfo, new AlienProtocol());
			alienReaderAdapter.connect(omg);
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			alienReaderAdapter.disconnect();
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * @param commSer
	 *            the commSer to set
	 */
	@Inject
	public void setCommSer(CommunicationService commSer) {
		this.commSer = commSer;
	}
}
