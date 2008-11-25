/*
 *  Fixed_ALEFieldTests.java
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
import org.rifidi.edge.ale.datatypes.builtin.formats.EPCFormats;
import org.rifidi.edge.ale.datatypes.builtin.formats.StringFormats;
import org.rifidi.edge.ale.datatypes.builtin.formats.UIntFormats;
import org.rifidi.edge.ale.fields.generic.Fixed_ALEField;
import org.rifidi.edge.tags.util.BitVector;
import org.rifidi.edge.tags.util.ConvertingUtil;

/**
 * @author kyle
 * 
 */
public class Fixed_ALEFieldTests {

	@Test
	public void testDefault() {
		String hexInteger = "83";
		BitVector bv = new BitVector(hexInteger, 16);
		Fixed_ALEField field = new Fixed_ALEField(bv);
		Assert.assertEquals("x" + hexInteger, field.getData());
	}

	@Test
	public void testBits() {
		String bits = "00000010101010001010100100001";
		BitVector bv = new BitVector(bits, 2);
		Fixed_ALEField field = new Fixed_ALEField(bv);
		String returnString = bits.length()
				+ ":x"
				+ ConvertingUtil.toString(bits, 2, 16, ConvertingUtil
						.roundUpDivision(bits.length(), 4));
		Assert.assertEquals(returnString, field.getData(ALEDataTypes.BITS,
				BitsFormats.HEX));
	}

	@Test
	public void testString() {
		String bits = "00000010101010001010100100001";
		BitVector bv = new BitVector(bits, 2);
		Fixed_ALEField field = new Fixed_ALEField(bv);
		boolean error = false;
		try {
			field.getData(ALEDataTypes.STRING, StringFormats.STRING);
		} catch (IllegalArgumentException ex) {
			error = true;
		}
		Assert.assertTrue(error);
	}

	@Test
	public void testEPC() {
		String bits = "000000010101010100010101001";
		BitVector bv = new BitVector(bits, 2);
		Fixed_ALEField field = new Fixed_ALEField(bv);
		String returnString = field.getData(ALEDataTypes.EPC,
				EPCFormats.EPC_HEX);
		String answer = "urn:epc:raw:"
				+ bits.length()
				+ ".x"
				+ ConvertingUtil.toString(bits, 2, 16,
						ConvertingUtil.roundUpDivision(bits.length(), 4))
						.toUpperCase();
		Assert.assertEquals(answer, returnString);
	}

	@Test
	public void testUint() {
		String hexInteger = "83";
		BitVector bv = new BitVector(hexInteger, 16);
		Fixed_ALEField field = new Fixed_ALEField(bv);
		Assert.assertEquals("x" + hexInteger, field.getData(ALEDataTypes.UINT, UIntFormats.HEX));
	}

}
