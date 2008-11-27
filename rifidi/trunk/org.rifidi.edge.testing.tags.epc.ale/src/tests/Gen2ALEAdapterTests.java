/*
 *  Gen2ALEAdapterTests.java
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
import org.rifidi.edge.ale.exceptions.ALEException;
import org.rifidi.edge.ale.fields.builtin.AFI_ALEField;
import org.rifidi.edge.ale.fields.builtin.AccessPwd_ALEField;
import org.rifidi.edge.ale.fields.builtin.EPCBank_ALEField;
import org.rifidi.edge.ale.fields.builtin.EPC_ALEField;
import org.rifidi.edge.ale.fields.builtin.KillPwd_ALEField;
import org.rifidi.edge.ale.fields.builtin.NSI_ALEField;
import org.rifidi.edge.ale.fields.builtin.TIDBank_ALEField;
import org.rifidi.edge.ale.fields.builtin.UserBank_ALEField;
import org.rifidi.edge.ale.fields.generic.FixedAddress;
import org.rifidi.edge.ale.fields.generic.Fixed_ALEField;
import org.rifidi.edge.tags.epc.ale.c1g2.C1G2ALEPhysicalTagModelAdapter;
import org.rifidi.edge.tags.epc.c1g2.C1G2EPCBankWithHeader;
import org.rifidi.edge.tags.epc.c1g2.C1G2PhysicalTagModel;
import org.rifidi.edge.tags.epc.c1g2.C1G2ReservedBank;
import org.rifidi.edge.tags.epc.c1g2.C1G2TIDBank;
import org.rifidi.edge.tags.epc.c1g2.C1G2UserBank;
import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * @author kyle
 * 
 */
public class Gen2ALEAdapterTests {

	public static final String BITS = "001100000000100000110011101100101101110111011001000000010100000000110101000001010000000000000000";
	public static final String PURE = "urn:epc:id:sgtin:0867360217.005.889520128";
	public static final String TAG = "urn:epc:tag:sgtin-96:0.0867360217.005.889520128";
	public static final String RAW = "urn:epc:raw:96.x300833B2DDD9014035050000";

	public static final String CRC = "0000" + "0000" + "0000" + "0000";
	public static final String TOGGLE = "1";
	public static final String Length = "00101";
	public static final String RFU = "00";
	public static final String AFI = "01100111";
	public static final String NSI = TOGGLE + AFI;
	public static final String EPC_BANK = CRC + Length + RFU + NSI + BITS;

	// reserved Mem Bank = 0000000F000000F0
	public static final String RESERVED_MB = "0000" + "0000" + "0000" + "0000"
			+ "0000" + "0000" + "0000" + "1111" + "" + "0000" + "0000" + "0000"
			+ "0000" + "0000" + "0000" + "1111" + "0000";

	public static final String TID_MB = "0101001001010101001010100111110";

	public void testIncorrectModel() {
		// TODO: send in C1G1 physical tag model, and make sure it fails
	}

	@Test
	public void testGetEPC() {
		// test with EPC only
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(BITS);
		try {
			EPC_ALEField epcField = adapter.getEPC(tagModel);
			Assert.assertEquals(TAG, epcField.getData());
		} catch (ALEException e) {
			Assert.fail();
		}

	}

	@Test
	public void testGetKillPwd() {
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2ReservedBank reservedMB = new C1G2ReservedBank(RESERVED_MB);
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(reservedMB,
				null, null, null);
		try {
			KillPwd_ALEField field = adapter.getKillPwd(tagModel);
			String killpwd = "xF";
			Assert.assertEquals(killpwd, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testGetAccessPwd() {
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2ReservedBank reservedMB = new C1G2ReservedBank(RESERVED_MB);
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(reservedMB,
				null, null, null);
		try {
			AccessPwd_ALEField field = adapter.getAccessPwd(tagModel);
			String killpwd = "xF0";
			Assert.assertEquals(killpwd, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testGetEPCBank() {
		// test with EPC only
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(BITS);
		try {
			EPCBank_ALEField field = adapter.getEPCBank(tagModel);
			String mbContents = "x"
					+ ConvertingUtil.toString(BITS, 2, 16,
							ConvertingUtil.roundUpDivision(BITS.length(), 4))
							.toUpperCase();
			Assert.assertEquals(mbContents, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}

	}

	@Test
	public void testTIDBank() {
		// test with EPC only
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2TIDBank tidBank = new C1G2TIDBank(TID_MB);
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(null, null,
				tidBank, null);
		try {
			TIDBank_ALEField field = adapter.getTIDBank(tagModel);
			String mbContents = "x"
					+ ConvertingUtil.toString(TID_MB, 2, 16,
							ConvertingUtil.roundUpDivision(TID_MB.length(), 4))
							.toUpperCase();
			Assert.assertEquals(mbContents, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testUserBank() {
		// test with EPC only
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2UserBank userBank = new C1G2UserBank(TID_MB);
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(null, null,
				null, userBank);
		try {
			UserBank_ALEField field = adapter.getUserBank(tagModel);
			String mbContents = "x"
					+ ConvertingUtil.toString(TID_MB, 2, 16,
							ConvertingUtil.roundUpDivision(TID_MB.length(), 4))
							.toUpperCase();
			Assert.assertEquals(mbContents, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testAFI() {
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2EPCBankWithHeader epcBank = new C1G2EPCBankWithHeader(EPC_BANK);
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(null, epcBank,
				null, null);
		try {
			AFI_ALEField field = adapter.getAFI(tagModel);
			String afi = "x" + ConvertingUtil.toString(AFI, 2, 16, 2);
			Assert.assertEquals(afi, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testNSI() {
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2EPCBankWithHeader epcBank = new C1G2EPCBankWithHeader(EPC_BANK);
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(null, epcBank,
				null, null);
		try {
			NSI_ALEField field = adapter.getNSI(tagModel);
			String afi = "x" + ConvertingUtil.toString(NSI, 2, 16, 3);
			Assert.assertEquals(afi, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testAbsoluteBank0() {
		FixedAddress address = new FixedAddress(0, 32, 0);
		String answer = RESERVED_MB.substring(0, 32);
		answer = "x" + ConvertingUtil.toString(answer, 2, 16, 0).toUpperCase();
		try {
			Assert.assertEquals(answer, testAbsoluteAddressBank0(address)
					.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testAbsoluteBank1() {
		FixedAddress address = new FixedAddress(1, 24, 8);
		String answer = EPC_BANK.substring(8, 32);
		answer = "x" + ConvertingUtil.toString(answer, 2, 16, 0).toUpperCase();
		try {
			Assert.assertEquals(answer, testAbsoluteAddressBank0(address)
					.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testAbsoluteBank2() {
		FixedAddress address = new FixedAddress(2, 8, 0);
		String answer = TID_MB.substring(0, 8);
		answer = "x" + ConvertingUtil.toString(answer, 2, 16, 0).toUpperCase();
		try {
			Assert.assertEquals(answer, testAbsoluteAddressBank0(address)
					.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testAbsoluteBank3() {
		FixedAddress address = new FixedAddress(3, 8, 0);
		String answer = TID_MB.substring(0, 8);
		answer = "x" + ConvertingUtil.toString(answer, 2, 16, 0).toUpperCase();
		try {
			Assert.assertEquals(answer, testAbsoluteAddressBank0(address)
					.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}

	@Test
	public void testAbsoluteBank5() {
		FixedAddress address = new FixedAddress(5, 8, 0);
		boolean error = false;
		try {
			testAbsoluteAddressBank0(address);
		} catch (ALEException e) {
			error = true;
		}
		Assert.assertTrue(error);
	}

	public Fixed_ALEField testAbsoluteAddressBank0(FixedAddress address)
			throws ALEException {
		C1G2ALEPhysicalTagModelAdapter adapter = new C1G2ALEPhysicalTagModelAdapter();
		C1G2ReservedBank reservedBank = new C1G2ReservedBank(RESERVED_MB);
		C1G2EPCBankWithHeader epcBank = new C1G2EPCBankWithHeader(EPC_BANK);
		C1G2TIDBank tidBank = new C1G2TIDBank(TID_MB);
		C1G2UserBank userBank = new C1G2UserBank(TID_MB);
		C1G2PhysicalTagModel tagModel = new C1G2PhysicalTagModel(reservedBank,
				epcBank, tidBank, userBank);
		return adapter.getFixed(tagModel, address);
	}

}
