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

import org.junit.Test;
import org.rifidi.edge.tags.data.memorybank.MemoryBank;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;

/**
 * @author kyle
 * 
 */
public class MemoryBankTest {

	@Test
	public void testMemoryBank() {
		String bits = "0101010100101010010111110";
		MemoryBank mb = new TestBank(bits);
		Assert.assertEquals(bits, mb.toString());
	}

	@Test
	public void testAccess() {
		String bits = "0001001010001010001011011";
		MemoryBank mb = new TestBank(bits);

		// these tests should not have exceptions
		try {
			Assert.assertEquals(bits.substring(0, 5), mb.access(5, 0).toString(
					2));
			Assert.assertEquals("", mb.access(0, 0).toString(2));
			Assert.assertEquals(bits, mb.access(bits.length(), 0).toString(2));
			Assert.assertEquals(bits.substring(5, 10), mb.access(5, 5)
					.toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}

		// these tests should all have exceptions
		boolean exception = false;
		try {
			mb.access(100, 0);
		} catch (IllegalBankAccessException e) {
			exception = true;
		}
		Assert.assertTrue(exception);

		exception = false;
		try {
			mb.access(-1, 3);
		} catch (IllegalBankAccessException e) {
			exception = true;
		}
		Assert.assertTrue(exception);

		exception = false;
		try {
			mb.access(0, 100);
		} catch (IllegalBankAccessException e) {
			exception = true;
		}
		Assert.assertTrue(exception);
	}

	private class TestBank extends MemoryBank {
		private static final long serialVersionUID = 1L;

		public TestBank(String bits) {
			super.setMemoryBank(bits);
		}
	}

}
