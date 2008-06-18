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

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionIllegalStateException;
import org.rifidi.edge.core.exception.readerConnection.RifidiConnectionException;
import org.rifidi.edge.core.tag.TagRead;
import org.rifidi.edge.readerPlugin.alien.AlienReaderInfo;
import org.rifidi.edge.readerPlugin.alien.AlienReaderPlugin;

/**
 * 
 * 
 * @author Matt
 */
public class AlienReaderAdapterTest {

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
	 * @throws RifidiConnectionIllegalStateException 
	 */
	@Test
	public void testConnect() {
		AlienReaderInfo connectionInfo = new AlienReaderInfo();
		connectionInfo.setIPAddress("192.168.1.100");
		connectionInfo.setPort(23);
		connectionInfo.setUsername("alien");
		connectionInfo.setPassword("password");

		AlienReaderPlugin alienReaderAdapter = new AlienReaderPlugin(
				connectionInfo);
		try {
			alienReaderAdapter.connect();
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		}

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

		Assert.fail();
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
			alienReaderAdapter.connect();
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		}


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


		Assert.fail();
	}

	/**
	 * Tests the raw command sending to the reader
	 */
	@Test
	public void testRawCommand() {
		Assert.fail();
	}

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
			alienReaderAdapter.connect();
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		}

		try {
			alienReaderAdapter.disconnect();
		} catch (RifidiConnectionException e) {
			e.printStackTrace();
			Assert.fail();
		}
		
		Assert.fail();
	}
}
