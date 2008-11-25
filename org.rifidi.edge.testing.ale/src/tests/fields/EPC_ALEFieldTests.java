/*
 *  EPC_ALEFieldTests.java
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
import org.junit.Test;
import org.rifidi.edge.ale.datatypes.ALEDataTypes;
import org.rifidi.edge.ale.datatypes.builtin.formats.BitsFormats;
import org.rifidi.edge.ale.datatypes.builtin.formats.StringFormats;
import org.rifidi.edge.ale.datatypes.builtin.formats.UIntFormats;
import org.rifidi.edge.ale.fields.builtin.EPC_ALEField;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author kyle
 * 
 */
public class EPC_ALEFieldTests {

	public static String TAG = "urn:epc:tag:sgtin-96:0.0867360217.005.889520128";
	public static String BITS = "001100000000100000110011101100101101110111011001000000010100000000110101000001010000000000000000";

	@Test
	public void testDefault() {
		EPC_ALEField field = new EPC_ALEField(new BitVector(BITS, 2), null);
		Assert.assertEquals(TAG, field.getData());
	}

	@Test
	public void testBits() {
		EPC_ALEField field = new EPC_ALEField(new BitVector(BITS, 2), null);
		boolean failed = false;
		try {
			field.getData(ALEDataTypes.BITS, BitsFormats.HEX);
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}

	@Test
	public void testString() {
		EPC_ALEField field = new EPC_ALEField(new BitVector(BITS, 2), null);
		boolean failed = false;
		try {
			field.getData(ALEDataTypes.STRING, StringFormats.STRING);
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}

	@Test
	public void testEPC() {
		EPC_ALEField field = new EPC_ALEField(new BitVector(BITS, 2), null);
		Assert.assertEquals(TAG, field.getData());
	}

	@Test
	public void testUint() {
		EPC_ALEField field = new EPC_ALEField(new BitVector(BITS, 2), null);
		boolean failed = false;
		try {
			field.getData(ALEDataTypes.UINT, UIntFormats.HEX);
		} catch (IllegalArgumentException e) {
			failed = true;
		}
		Assert.assertTrue(failed);
	}

}
