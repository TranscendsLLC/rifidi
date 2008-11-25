/*
 *  AbstractUintDefaultFieldTests.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.fields;

import org.junit.Assert;
import org.rifidi.edge.ale.datatypes.ALEDataTypes;
import org.rifidi.edge.ale.datatypes.builtin.formats.BitsFormats;
import org.rifidi.edge.ale.datatypes.builtin.formats.EPCFormats;
import org.rifidi.edge.ale.datatypes.builtin.formats.StringFormats;
import org.rifidi.edge.ale.datatypes.builtin.formats.UIntFormats;
import org.rifidi.edge.ale.fields.ALEField;
import org.rifidi.edge.tags.util.BitVector;
import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * This class tests ALE Fields whose default and only valid datatype is UInt,
 * specifically killPwd, accessPwd, epcBank, tidBank, userBank, afi and nsi
 * 
 * @author kyle
 * 
 */
public abstract class AbstractUintDefaultFieldTests {

	private static final String BITS = "00101110";
	private static String asHex;

	public AbstractUintDefaultFieldTests() {
		asHex = "x"
				+ ConvertingUtil.toString(BITS, 2, 16,
						ConvertingUtil.roundUpDivision(BITS.length(), 4))
						.toUpperCase();
	}

	protected abstract ALEField getField(BitVector bv);
	
	public abstract void testDefault();
	
	public abstract void testBits();
	
	public abstract void testString();
	
	public abstract void testEPC();
	
	public abstract void testUint();

	public void doTestDefault() {
		ALEField field = getField(new BitVector(BITS, 2));
		Assert.assertEquals(asHex, field.getData());
	}

	public void doTestBits() {
		ALEField field = getField(new BitVector(BITS, 2));
		boolean failed = false;
		try {
			field.getData(ALEDataTypes.BITS, BitsFormats.HEX);
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}

	public void doTestString() {
		ALEField field = getField(new BitVector(BITS, 2));
		boolean failed = false;
		try {
			field.getData(ALEDataTypes.STRING, StringFormats.STRING);
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}

	public void doTestEPC() {
		ALEField field = getField(new BitVector(BITS, 2));
		boolean failed = false;
		try {
			field.getData(ALEDataTypes.EPC, EPCFormats.EPC_DECIMAL);
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}

	public void doTestUint() {
		ALEField field = getField(new BitVector(BITS, 2));
		Assert.assertEquals(asHex, field.getData(ALEDataTypes.UINT,
				UIntFormats.HEX));
	}

}
