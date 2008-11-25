/*
 *  TIDMemoryBankTest.java
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
import org.rifidi.edge.tags.epc.c1g2.C1G2TIDBank;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;


/**
 * @author kyle
 *
 */
public class TIDMemoryBankTest {
	
	public static final String ACI = "00000000";
	public static final String VENDOR_BITS = "1111111111111";
	public static final String MEMORY_BANK = ACI + VENDOR_BITS;
	
	@Test
	public void testACI(){
		C1G2TIDBank bank = new C1G2TIDBank(MEMORY_BANK);
		Assert.assertEquals(ACI, bank.getAllocationClassIdentifierBits().toString(2));
	}
	
	@Test
	public void testVendorBits(){
		C1G2TIDBank bank = new C1G2TIDBank(MEMORY_BANK);
		try {
			Assert.assertEquals(VENDOR_BITS, bank.getVendorDefinedBits().toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testMemoryBank(){
		C1G2TIDBank bank = new C1G2TIDBank(MEMORY_BANK);
		Assert.assertEquals(MEMORY_BANK, bank.toString());
	}

}
