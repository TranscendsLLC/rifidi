/*
 *  C1G1EPCBankTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.c1g1;

import junit.framework.Assert;

import org.junit.Test;
import org.rifidi.edge.tags.epc.c1g1.C1G1EPCBank;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;

/**
 * @author kyle
 * 
 */
public class C1G1EPCBankTest {
	private static final String EPC = "010101001010100101000";

	@Test
	public void testGetBank() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		try {
			Assert.assertEquals(EPC, bank.getEPC().toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}

	@Test
	public void testGetBankEmpty() {
		C1G1EPCBank bank = new C1G1EPCBank("");
		try {
			Assert.assertEquals("", bank.getEPC().toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}

	@Test
	public void testC1G2Size() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		Assert.assertEquals(EPC.length(), bank.getMemoryBankSize());
	}
	
	public void testToString(){
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		Assert.assertEquals(EPC, bank.toString());
	}

}
