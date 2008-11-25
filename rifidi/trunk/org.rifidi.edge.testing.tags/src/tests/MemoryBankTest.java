/*
 *  MemoryBankTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests;


import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.tags.data.memorybank.MemoryBank;

/**
 * @author kyle
 *
 */
public class MemoryBankTest {

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
	
	@Test
	public void testMemoryBank(){
		String bits = "0101010100101010010111110";
		MemoryBank mb = new MemoryBank(bits);
		Assert.assertEquals(bits, mb.toString());
	}

}
