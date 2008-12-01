/*
 *  EPCMemoryBankTestWithoutHeaderNSI.java
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

/**
 * @author kyle
 * 
 */
public class EPCMemoryBankTestWithoutHeaderNSI {

	private static final String CRC = "0000000000000000";
	private static final Boolean toggle = true;
	private static final String TOGGLE_BIT = "1";
	private static final String AFI = "01001010";
	private static final String EPC = "010101001010100101000";
	private static final String LENGTH = "00010";
	private static final String PC = LENGTH + "00" + TOGGLE_BIT + AFI; 

	@Test
	public void testIncorrectConstructor() {
		boolean error = false;
		try {
			new C1G2EPCBankWithoutHeader(EPC, AFI);
		} catch (IllegalArgumentException e) {
			error = true;
		}
		Assert.assertTrue(error);
	}

	@Test
	public void testCRC() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);
		// TODO: The CRC is not currenly being calculated
		Assert.assertEquals(CRC, bank.getCRCBits().toString(2));
	}

	@Test
	public void testLengthBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);
		Assert.assertEquals(LENGTH, bank.getLengthBits().toString(2));
	}

	@Test
	public void testRFUBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);
		Assert.assertEquals("00", bank.getRFUBits().toString(2));
	}

	@Test
	public void testToggleBit() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);

		Assert.assertEquals(toggle, bank.getToggleBit());

	}

	@Test
	public void testAFIBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);

		Assert.assertEquals(AFI, bank.getAFIBits().toString(2));

	}

	@Test
	public void testPCBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);
		String PC = EPCMemoryBankWithoutHeaderTest.getLengthBits(EPC) + "00"
				+ TOGGLE_BIT + AFI;

		Assert.assertEquals(PC, bank.getPCBits().toString(2));

	}

	@Test
	public void testEPCBits() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);
		try {
			Assert.assertEquals(EPC, bank.getEPCBits().toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}

	@Test
	public void testBank() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);
		String returnval = bank.toString();
		Assert.assertEquals(CRC+PC+EPC, returnval);
	}

	public void testGetNSI() {
		C1G2EPCBankWithoutHeader bank = new C1G2EPCBankWithoutHeader(EPC,
				TOGGLE_BIT + AFI);
		try {
			Assert
					.assertEquals(TOGGLE_BIT + AFI, bank.getNSIBits().toString(
							2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}

}
