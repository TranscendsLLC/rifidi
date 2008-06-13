/**
 * 
 */
package org.rifidi.edge.adapter.alien.test;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.adapter.alien.AlienConnectionInfo;
import org.rifidi.edge.adapter.alien.AlienReaderAdapter;
import org.rifidi.edge.common.utilities.converter.ByteAndHexConvertingUtility;
import org.rifidi.edge.core.exception.adapter.RifidiAdapterIllegalStateException;
import org.rifidi.edge.core.exception.adapter.RifidiConnectionException;
import org.rifidi.edge.core.tag.TagRead;

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
	 * @throws RifidiAdapterIllegalStateException 
	 */
	@Test
	public void testConnect() {
		AlienConnectionInfo connectionInfo = new AlienConnectionInfo();
		connectionInfo.setIPAddress("192.168.1.100");
		connectionInfo.setPort(23);
		connectionInfo.setUsername("alien");
		connectionInfo.setPassword("password");

		AlienReaderAdapter alienReaderAdapter = new AlienReaderAdapter(
				connectionInfo);
		try {
			alienReaderAdapter.connect();
		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}

		List<TagRead> tagList = null;
		try {
			tagList = alienReaderAdapter.getNextTags();
		} catch (RifidiAdapterIllegalStateException e) {
			// TODO Auto-generated catch block
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
		AlienConnectionInfo connectionInfo = new AlienConnectionInfo();
		connectionInfo.setIPAddress("192.168.1.100");
		connectionInfo.setPort(23);
		connectionInfo.setUsername("alien");
		connectionInfo.setPassword("password");

		AlienReaderAdapter alienReaderAdapter = new AlienReaderAdapter(
				connectionInfo);
		
		if (alienReaderAdapter == null)
			System.out.println("the readeradapter is null");
		
		try {
			alienReaderAdapter.connect();
		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}


		List<TagRead> tagList = null;
		try {
			tagList = alienReaderAdapter.getNextTags();
		} catch (RifidiAdapterIllegalStateException e) {
			// TODO Auto-generated catch block
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
		AlienConnectionInfo connectionInfo = new AlienConnectionInfo();
		connectionInfo.setIPAddress("192.168.1.100");
		connectionInfo.setPort(23);
		connectionInfo.setUsername("alien");
		connectionInfo.setPassword("password");

		AlienReaderAdapter alienReaderAdapter = new AlienReaderAdapter(
				connectionInfo);
		try {
			alienReaderAdapter.connect();
		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}

		try {
			alienReaderAdapter.disconnect();
		} catch (RifidiConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Assert.fail();
		}
		
		Assert.fail();
	}
}
