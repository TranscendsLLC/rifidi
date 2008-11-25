/*
 *  Vairable_ALEFieldTest.java
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
import org.rifidi.edge.ale.fields.generic.Variable_ALEField;

/**
 * @author kyle
 * 
 */
public class Variable_ALEFieldTests {

	@Test
	public void testDefault() {
		String bits = "00000010101010001010100100001";
		Variable_ALEField field = new Variable_ALEField(bits);
		Assert.assertEquals(bits, field.getData());

	}

	@Test
	public void testBits() {
		String bits = "00000010101010001010100100001";
		Variable_ALEField field = new Variable_ALEField(bits);
		boolean error = false;
		try {
			field.getData(ALEDataTypes.BITS, BitsFormats.HEX);
		} catch (IllegalArgumentException ex) {
			error = true;
		}
		Assert.assertTrue(error);
	}

	@Test
	public void testString() {
		String bits = "00000010101010001010100100001";
		Variable_ALEField field = new Variable_ALEField(bits);
		Assert.assertEquals(bits, field.getData(ALEDataTypes.STRING,
				StringFormats.STRING));

	}

	@Test
	public void testEPC() {
		String bits = "00000010101010001010100100001";
		Variable_ALEField field = new Variable_ALEField(bits);
		boolean error = false;
		try {
			field.getData(ALEDataTypes.EPC, EPCFormats.EPC_DECIMAL);
		} catch (IllegalArgumentException ex) {
			error = true;
		}
		Assert.assertTrue(error);
	}

	@Test
	public void testUint() {
		String bits = "00000010101010001010100100001";
		Variable_ALEField field = new Variable_ALEField(bits);
		boolean error = false;
		try {
			field.getData(ALEDataTypes.UINT, UIntFormats.DECIMAL);
		} catch (IllegalArgumentException ex) {
			error = true;
		}
		Assert.assertTrue(error);
	}

}
