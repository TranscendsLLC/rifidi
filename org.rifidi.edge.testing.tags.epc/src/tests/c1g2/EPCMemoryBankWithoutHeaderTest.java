/*
 *  EPCMemoryBankWithoutHeaderTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.c1g2;

import junit.framework.Assert;

import org.junit.Test;
import org.rifidi.edge.tags.epc.c1g2.C1G2EPCBankWithoutHeader;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * This class tests the EPCMemoryBankWithoutHeader using the constructor that
 * generates a default NSI
 * 
 * @author kyle
 * 
 */
public class EPCMemoryBankWithoutHeaderTest {

	/**
	 * This is a generated CRC for the default PC bits and the EPC 
	 */
	private static final String CRC = "0000000000000000";
	private static final Boolean toggle = false;
	private static final String TOGGLE_BIT = "0";
	private static final String AFI = "00000000";
	private static final String LENGTH = "00010";
	/*
	 * x54A947
	 */
	private static final String EPC = "0101"+"0100"+"1010"+"1001"+"0100"+"0111";
	
	/*
	 * x1000
	 * this is a generated PC for the EPC
	 */
	private static final String PC = LENGTH + "00" + TOGGLE_BIT + AFI; 

	@Test
	public void testCRC() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC);
		// TODO: The CRC is not currenly being calculated
		Assert.assertEquals(CRC, bank.getCRCBits().toString(2));
	}

	@Test
	public void testLengthBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC);
		Assert.assertEquals(LENGTH, bank.getLengthBits().toString(2));
	}

	@Test
	public void testRFUBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC);
		Assert.assertEquals("00", bank.getRFUBits().toString(2));
	}

	@Test
	public void testToggleBit() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC);
		Assert.assertEquals(toggle, bank.getToggleBit());
	}

	@Test
	public void testAFIBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC);

		Assert.assertEquals(AFI, bank.getAFIBits().toString(2));

	}

	@Test
	public void testPCBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC);
		String PC = getLengthBits(EPC) + "00" + TOGGLE_BIT + AFI;

		Assert.assertEquals(PC, bank.getPCBits().toString(2));

	}

	@Test
	public void testEPCBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC);

		try {
			Assert.assertEquals(EPC, bank.getEPCBits().toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}

	}

	@Test
	public void testBank() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC);
		String returnval = bank.toString();
		Assert.assertEquals(CRC+PC+EPC, returnval);
	}

	public static String getLengthBits(String EPC_IN) {
		int numBits = EPC_IN.length();
		int L = ConvertingUtil.roundUpDivision(numBits, 16);
		return ConvertingUtil.toString(Integer.toString(L), 10, 2, 5);
	}

}
