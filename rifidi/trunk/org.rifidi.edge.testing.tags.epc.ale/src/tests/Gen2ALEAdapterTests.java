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
import org.rifidi.edge.ale.exceptions.FieldNotFoundALEException;
import org.rifidi.edge.ale.fields.builtin.AccessPwd_ALEField;
import org.rifidi.edge.ale.fields.builtin.EPCBank_ALEField;
import org.rifidi.edge.ale.fields.builtin.EPC_ALEField;
import org.rifidi.edge.ale.fields.builtin.KillPwd_ALEField;
import org.rifidi.edge.tags.epc.ale.c1g2.C1G2ALEPhysicalTagModelAdapter;
import org.rifidi.edge.tags.epc.c1g2.C1G2PhysicalTagModel;
import org.rifidi.edge.tags.epc.c1g2.C1G2ReservedBank;
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

	// reserved Mem Bank = 0000000F000000F0
	public static final String RESERVED_MB = "0000" + "0000" + "0000" + "0000"
			+ "0000" + "0000" + "0000" + "1111" + "" + "0000" + "0000" + "0000"
			+ "0000" + "0000" + "0000" + "1111" + "0000";

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
		} catch (FieldNotFoundALEException e) {
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
		} catch (FieldNotFoundALEException e) {
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
		} catch (FieldNotFoundALEException e) {
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
					+ ConvertingUtil.toString(BITS, 2, 16, ConvertingUtil
							.roundUpDivision(BITS.length(), 4)).toUpperCase();
			Assert.assertEquals(mbContents, field.getData());
		} catch (FieldNotFoundALEException e) {
			Assert.fail();
		}

	}

}
