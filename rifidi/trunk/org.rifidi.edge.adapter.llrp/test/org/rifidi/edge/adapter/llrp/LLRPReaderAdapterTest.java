package org.rifidi.edge.adapter.llrp;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LLRPReaderAdapterTest extends TestCase {
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
	 */
	@Test
	public void testConnect() {
		LLRPConnectionInfo lci = new LLRPConnectionInfo();
		lci.setIPAddress("127.0.0.1");
		lci.setPort(10000);
		Assert.fail();
	}
	
	/**
	 * Tests the tag streaming portion of the code
	 */
	@Test
	public void testStream() {
		
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
		
		Assert.fail();
	}
}
