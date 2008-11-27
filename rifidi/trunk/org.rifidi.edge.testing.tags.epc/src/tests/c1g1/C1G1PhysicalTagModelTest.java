/*
 *  C1G1PhysicalTagModelTest.java
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
import org.rifidi.edge.tags.epc.c1g1.C1G1PhysicalTagModel;
import org.rifidi.edge.tags.exceptions.IllegalBankAccessException;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author kyle
 * 
 */
public class C1G1PhysicalTagModelTest {
	private static final String EPC = "010101001010100101000";

	@Test
	public void testGetEPC() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tag = new C1G1PhysicalTagModel(bank);
		try {
			Assert.assertEquals(bank, tag.getEPCBank());
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}

	@Test
	public void testTagType() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tag = new C1G1PhysicalTagModel(bank);
		Assert.assertEquals(C1G1PhysicalTagModel.class.getCanonicalName(), tag
				.getTagType());
	}
	
	@Test
	public void testReadMB() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tag = new C1G1PhysicalTagModel(bank);
		try {
			BitVector redBits = tag.read(0, 8, 0);
			Assert.assertEquals(EPC.substring(0,8), redBits.toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testReadMBFail() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tag = new C1G1PhysicalTagModel(bank);
		boolean error=false;
		try {
			tag.read(1, 8, 0);
		} catch (IllegalBankAccessException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testReadWholeMB() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tag = new C1G1PhysicalTagModel(bank);
		try {
			BitVector redBits = tag.read(0, EPC.length(), 0);
			Assert.assertEquals(EPC, redBits.toString(2));
		} catch (IllegalBankAccessException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testReadTooMuch() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tag = new C1G1PhysicalTagModel(bank);
		boolean error=false;
		try {
			tag.read(0, EPC.length()+10, 0);
		} catch (IllegalBankAccessException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testReadInvalidOffset() {
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tag = new C1G1PhysicalTagModel(bank);
		boolean error=false;
		try {
			tag.read(0, 5, EPC.length()+10);
		} catch (IllegalBankAccessException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}

}
