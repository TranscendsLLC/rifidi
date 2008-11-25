/*
 *  UintDataType.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.datatypes;

import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.rifidi.edge.ale.datatypes.builtin.UnsignedInteger_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.UIntFormats;
import org.rifidi.edge.ale.datatypes.builtin.patterns.uint.UintPattern;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author kyle
 * 
 */
public class UintDataTypeTests {

	public static String int_1 = "135";
	public static String hex_1 = "x87";

	public static String int_2 = "0";
	public static String hex_2 = "x0";

	@Test
	public void testNumOne() {
		UnsignedInteger_ALEDataType dt = new UnsignedInteger_ALEDataType(int_1);
		Assert.assertEquals(hex_1, dt.getData(UIntFormats.HEX));
		Assert.assertEquals(int_1, dt.getData(UIntFormats.DECIMAL));

		dt = new UnsignedInteger_ALEDataType(hex_1);
		Assert.assertEquals(int_1, dt.getData(UIntFormats.DECIMAL));
		Assert.assertEquals(hex_1, dt.getData(UIntFormats.HEX));
	}

	@Test
	public void testNum2() {
		UnsignedInteger_ALEDataType dt = new UnsignedInteger_ALEDataType(int_2);
		Assert.assertEquals(hex_2, dt.getData(UIntFormats.HEX));
		Assert.assertEquals(int_2, dt.getData(UIntFormats.DECIMAL));

		dt = new UnsignedInteger_ALEDataType(hex_2);
		Assert.assertEquals(int_2, dt.getData(UIntFormats.DECIMAL));
		Assert.assertEquals(hex_2, dt.getData(UIntFormats.HEX));
	}

	@Test
	public void testBitVectorConstructor() {
		BitVector bv = new BitVector(int_1, 10);
		UnsignedInteger_ALEDataType dt = new UnsignedInteger_ALEDataType(bv);
		Assert.assertEquals(hex_1, dt.getData(UIntFormats.HEX));
	}

	@Test
	public void testHexUintPattern() {
		UintPattern pattern = UintPattern.create(hex_1);

		UnsignedInteger_ALEDataType dt = new UnsignedInteger_ALEDataType(hex_1);
		Assert.assertTrue(dt.matches(pattern));

		dt = new UnsignedInteger_ALEDataType(hex_2);
		Assert.assertFalse(dt.matches(pattern));
	}

	@Test
	public void testStarUintPattern() {
		UnsignedInteger_ALEDataType dt = new UnsignedInteger_ALEDataType(hex_1);
		UintPattern pattern = UintPattern.create("*");
		Assert.assertTrue(dt.matches(pattern));
	}

	@Test
	public void testHexRangePattern() {
		String rangePattern = "[x123-x456]";
		UintPattern pattern = UintPattern.create(rangePattern);

		UnsignedInteger_ALEDataType uint = new UnsignedInteger_ALEDataType(
				"x00");
		Assert.assertFalse(uint.matches(pattern));

		uint = new UnsignedInteger_ALEDataType("x123");
		Assert.assertTrue(uint.matches(pattern));

		uint = new UnsignedInteger_ALEDataType("x12F");
		Assert.assertTrue(uint.matches(pattern));

		uint = new UnsignedInteger_ALEDataType("x456");
		Assert.assertTrue(uint.matches(pattern));

		uint = new UnsignedInteger_ALEDataType("xFFF");
		Assert.assertFalse(uint.matches(pattern));
	}

	@Test
	public void testHexMaskPattern() {
		String num = "11101001";
		String mask = "00111000";
		String compare = "00101000";

		String numHex = "x" + new BigInteger(num, 2).toString(16);
		String maskHex = "x" + new BigInteger(mask, 2).toString(16);
		String compareHex = "x" + new BigInteger(compare, 2).toString(16);
		
		UnsignedInteger_ALEDataType uint = new UnsignedInteger_ALEDataType(
				numHex);
		UintPattern pattern = UintPattern.create("&"+maskHex+"="+compareHex);
		Assert.assertTrue(uint.matches(pattern));
		
		String num_2 = "11111001";
		String num_2Hex = "x"+new BigInteger(num_2, 2).toString(16);
		uint = new UnsignedInteger_ALEDataType(num_2Hex);
		Assert.assertFalse(uint.matches(pattern));
	}
	
	@Test
	public void testDecimalUintPattern(){
		UintPattern pattern = UintPattern.create(int_1);

		UnsignedInteger_ALEDataType dt = new UnsignedInteger_ALEDataType(hex_1);
		Assert.assertTrue(dt.matches(pattern));

		dt = new UnsignedInteger_ALEDataType(hex_2);
		Assert.assertFalse(dt.matches(pattern));
	}
	
	@Test
	public void testDecimalRangePattern() {
		String rangePattern = "[123-456]";
		UintPattern pattern = UintPattern.create(rangePattern);

		UnsignedInteger_ALEDataType uint = new UnsignedInteger_ALEDataType(
				"0");
		Assert.assertFalse(uint.matches(pattern));

		uint = new UnsignedInteger_ALEDataType("123");
		Assert.assertTrue(uint.matches(pattern));

		uint = new UnsignedInteger_ALEDataType("150");
		Assert.assertTrue(uint.matches(pattern));

		uint = new UnsignedInteger_ALEDataType("456");
		Assert.assertTrue(uint.matches(pattern));

		uint = new UnsignedInteger_ALEDataType("500");
		Assert.assertFalse(uint.matches(pattern));
	}

}
