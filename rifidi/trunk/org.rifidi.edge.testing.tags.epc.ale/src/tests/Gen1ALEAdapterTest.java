/*
 *  Gen1ALEAdapterTest.java
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
import org.rifidi.edge.ale.fields.builtin.EPC_ALEField;
import org.rifidi.edge.ale.fields.generic.FixedAddress;
import org.rifidi.edge.ale.fields.generic.Fixed_ALEField;
import org.rifidi.edge.ale.fields.generic.VariableAddress;
import org.rifidi.edge.tags.epc.ale.c1g1.C1G1ALEPhysicalTagModelAdapter;
import org.rifidi.edge.tags.epc.c1g1.C1G1EPCBank;
import org.rifidi.edge.tags.epc.c1g1.C1G1PhysicalTagModel;

/**
 * @author kyle
 *
 */
public class Gen1ALEAdapterTest {
	private static final String EPC = "0101"+"0100"+"1010"+"1001"+"0100"+"0111";
	private static final String HEX_NUM = "54A947";
	private static final String EPC_HEX = "urn:epc:raw:24.x"+HEX_NUM;
	
	@Test
	public void testEPC(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		try {
			EPC_ALEField field = adapter.getEPC(tagModel);
			
			Assert.assertEquals(EPC_HEX, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testKillPwd(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		boolean error =false;
		try {
			adapter.getKillPwd(tagModel);
		} catch (ALEException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testAccessPwd(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		boolean error =false;
		try {
			adapter.getAccessPwd(tagModel);
		} catch (ALEException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testEpcBank(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		boolean error =false;
		try {
			adapter.getEPCBank(tagModel);
		} catch (ALEException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testTidBank(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		boolean error =false;
		try {
			adapter.getTIDBank(tagModel);
		} catch (ALEException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testUserBank(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		boolean error =false;
		try {
			adapter.getUserBank(tagModel);
		} catch (ALEException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testAFI(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		boolean error =false;
		try {
			adapter.getAFI(tagModel);
		} catch (ALEException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testNSI(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		boolean error =false;
		try {
			adapter.getNSI(tagModel);
		} catch (ALEException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}
	
	@Test
	public void testFixedAddress(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		
		FixedAddress address = new FixedAddress(0,8,0);
		try {
			String expected = "x"+HEX_NUM.substring(0,2);
			Fixed_ALEField field = adapter.getFixed(tagModel, address);
			Assert.assertEquals(expected, field.getData());
		} catch (ALEException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testVariable(){
		C1G1ALEPhysicalTagModelAdapter adapter = new C1G1ALEPhysicalTagModelAdapter();
		C1G1EPCBank bank = new C1G1EPCBank(EPC);
		C1G1PhysicalTagModel tagModel = new C1G1PhysicalTagModel(bank);
		boolean error =false;
		try {
			adapter.getVariable(tagModel, new VariableAddress(0,"ace"));
		} catch (ALEException e) {
			error=true;
		}
		Assert.assertTrue(error);
	}

}
