/*
 *  EPCMemoryBankTest.java
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
import org.rifidi.edge.tags.epc.c1g2.C1G2EPCBankWithHeader;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;

/**
 * @author kyle
 * 
 */
public class EPCMemoryBankTest {

	public static final String CRC_BITS = "0110101100001101";
	public static final String LENGTH_BITS = "11010";
	public static final String RFU_BITS = "10";
	public static final String TOGGLE_BIT = "1";
	public static final String AFI_BITS = "01101010";
	public static final String PC_BITS = LENGTH_BITS + RFU_BITS + TOGGLE_BIT
			+ AFI_BITS;
	public static final String EPC_BITS = "0011000001110000"
			+ "0000000001001000" + "0100010000000110" + "0110010000000000"
			+ "0001011100001100" + "0010101010010001" + "0101001010010100";
	public static final String MEM_BANK = CRC_BITS + PC_BITS + EPC_BITS;

	@Test
	public void testCRC() {
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		Assert.assertEquals(CRC_BITS, bank.getCRCBits().toString(2));
	}

	@Test
	public void testLengthBits() {
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		Assert.assertEquals(LENGTH_BITS, bank.getLengthBits().toString(2));
	}
	
	@Test
	public void testRFUBits() {
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		Assert.assertEquals(RFU_BITS, bank.getRFUBits().toString(2));
	}
	
	@Test
	public void testToggleBit() {
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		Boolean toggle = false;
		if(TOGGLE_BIT.equals("1")){
			toggle = true;
		}
		Assert.assertEquals(toggle, bank.getToggleBit());
	}
	
	@Test
	public void testAFIBits() {
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		Assert.assertEquals(AFI_BITS, bank.getAFIBits().toString(2));
	}
	
	@Test
	public void testPCBits() {
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		Assert.assertEquals(PC_BITS, bank.getPCBits().toString(2));
	}
	
	@Test
	public void testEPCBits() {
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		try {
			Assert.assertEquals(EPC_BITS, bank.getEPCBits().toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testBank(){
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		String returnval = bank.toString();
		Assert.assertEquals(MEM_BANK, returnval);
	}
	
	public void testNSIAndEPC(){
		C1G2EPCBankWithHeader bank = new C1G2EPCBankWithHeader(MEM_BANK);
		try {
			Assert.assertEquals(TOGGLE_BIT + AFI_BITS + EPC_BITS, bank.getEPCBits().toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}

}
