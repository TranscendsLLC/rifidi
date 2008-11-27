/*
 *  C1G2PhysicalTagModelTest.java
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
import org.rifidi.edge.tags.epc.c1g2.C1G2PhysicalTagModel;
import org.rifidi.edge.tags.epc.c1g2.C1G2ReservedBank;
import org.rifidi.edge.tags.epc.c1g2.C1G2TIDBank;
import org.rifidi.edge.tags.epc.c1g2.C1G2UserBank;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;

/**
 * @author kyle
 * 
 */
public class C1G2PhysicalTagModelTest {

	@Test
	public void testNullConstructor() {
		C1G2PhysicalTagModel model = new C1G2PhysicalTagModel(null, null, null,
				null);
		boolean error = false;
		try {
			model.getEPCBank();
		} catch (IllegalBankAccessException e) {
			error = true;
		}
		Assert.assertTrue(error);

		error = false;
		try {
			model.getEPCBits();
		} catch (IllegalBankAccessException e) {
			error = true;
		}
		Assert.assertTrue(error);

		error = false;
		try {
			model.getReservedBank();
		} catch (IllegalBankAccessException e) {
			error = true;
		}
		Assert.assertTrue(error);

		error = false;
		try {
			model.getTIDBank();
		} catch (IllegalBankAccessException e) {
			error = true;
		}
		Assert.assertTrue(error);

		error = false;
		try {
			model.getUserBank();
		} catch (IllegalBankAccessException e) {
			error = true;
		}
		Assert.assertTrue(error);
	}

	@Test
	public void testNonNullConstructor() {
		C1G2ReservedBank reservedmb = new C1G2ReservedBank(
				ReservedMemoryBankTest.MEMORY_BANK);
		C1G2EPCBankWithHeader epcmb = new C1G2EPCBankWithHeader(
				EPCMemoryBankTest.MEM_BANK);
		C1G2TIDBank tidmb = new C1G2TIDBank(TIDMemoryBankTest.MEMORY_BANK);
		C1G2UserBank usermb = new C1G2UserBank(UserMemoryBankTest.MEMORY_BANK);
		C1G2PhysicalTagModel model = new C1G2PhysicalTagModel(reservedmb,
				epcmb, tidmb, usermb);

		try {
			Assert.assertEquals(reservedmb, model.getReservedBank());
			Assert.assertEquals(epcmb, model.getEPCBank());
			Assert.assertEquals(tidmb, model.getTIDBank());
			Assert.assertEquals(usermb, model.getUserBank());
			Assert.assertEquals(EPCMemoryBankTest.EPC_BITS, model.getEPCBits()
					.toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
		Assert.assertEquals(C1G2PhysicalTagModel.class.getCanonicalName(),
				model.getTagType());

	}

	@Test
	public void testEPCStringConstructor() {
		C1G2PhysicalTagModel model = new C1G2PhysicalTagModel(
				EPCMemoryBankTest.EPC_BITS);

		// these tests should work
		try {
			Assert.assertEquals(EPCMemoryBankTest.EPC_BITS, model.getEPCBits()
					.toString(2));
			Assert.assertEquals(EPCMemoryBankTest.EPC_BITS, model.getEPCBank()
					.getEPCBits().toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}

		// these test should throw an exception
		boolean error = false;
		try {
			model.getReservedBank();
		} catch (IllegalBankAccessException e) {
			error = true;
		}
		Assert.assertTrue(error);

		error = false;
		try {
			model.getTIDBank();
		} catch (IllegalBankAccessException e) {
			error = true;
		}
		Assert.assertTrue(error);

		error = false;
		try {
			model.getUserBank();
		} catch (IllegalBankAccessException e) {
			error = true;
		}
		Assert.assertTrue(error);
	}

	@Test
	public void testReadReserved() {
		C1G2ReservedBank reservedmb = new C1G2ReservedBank(
				ReservedMemoryBankTest.MEMORY_BANK);
		C1G2PhysicalTagModel model = new C1G2PhysicalTagModel(reservedmb, null,
				null, null);
		testReadMB(model, 0, 10, 10, ReservedMemoryBankTest.MEMORY_BANK
				.substring(10, 20));

	}

	@Test
	public void testReadEPC() {
		C1G2EPCBankWithHeader epcMB = new C1G2EPCBankWithHeader(
				EPCMemoryBankTest.MEM_BANK);
		C1G2PhysicalTagModel model = new C1G2PhysicalTagModel(null, epcMB,
				null, null);
		testReadMB(model, 1, 10, 10, EPCMemoryBankTest.MEM_BANK.substring(10,
				20));

	}

	@Test
	public void testReadTID() {
		C1G2TIDBank tidMB = new C1G2TIDBank(TIDMemoryBankTest.MEMORY_BANK);
		C1G2PhysicalTagModel model = new C1G2PhysicalTagModel(null, null,
				tidMB, null);
		testReadMB(model, 2, 10, 10, TIDMemoryBankTest.MEMORY_BANK.substring(
				10, 20));

	}

	@Test
	public void testReadUser() {
		C1G2UserBank userMB = new C1G2UserBank(UserMemoryBankTest.MEMORY_BANK);
		C1G2PhysicalTagModel model = new C1G2PhysicalTagModel(null, null, null,
				userMB);
		testReadMB(model, 3, 10, 10, UserMemoryBankTest.MEMORY_BANK.substring(
				10, 20));
	}

	@Test
	public void testReadUnknownMemBank() {
		C1G2ReservedBank reservedmb = new C1G2ReservedBank(
				ReservedMemoryBankTest.MEMORY_BANK);
		C1G2EPCBankWithHeader epcmb = new C1G2EPCBankWithHeader(
				EPCMemoryBankTest.MEM_BANK);
		C1G2TIDBank tidmb = new C1G2TIDBank(TIDMemoryBankTest.MEMORY_BANK);
		C1G2UserBank usermb = new C1G2UserBank(UserMemoryBankTest.MEMORY_BANK);
		C1G2PhysicalTagModel model = new C1G2PhysicalTagModel(reservedmb,
				epcmb, tidmb, usermb);
		
		boolean error=false;
		try {
			model.read(4, 10, 10);
		} catch (IllegalArgumentException e) {
			error=true;
		} catch (IllegalBankAccessException e) {

		}
		Assert.assertTrue(error);

	}

	private void testReadMB(C1G2PhysicalTagModel model, int mbNumber,
			int length, int offset, String returnBits) {
		// first test case where mb is null
		C1G2PhysicalTagModel nullModel = new C1G2PhysicalTagModel(null, null,
				null, null);
		boolean error = false;
		try {
			nullModel.read(mbNumber, length, offset);
		} catch (IllegalArgumentException e) {
			error = true;
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}

		Assert.assertTrue(error);

		// next test case where read is successful
		try {
			Assert.assertEquals(returnBits, model
					.read(mbNumber, length, offset).toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}

}
