/*
 *  BitsDataTypeTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.datatypes;

import org.junit.Assert;
import org.junit.Test;
import org.rifidi.edge.ale.datatypes.builtin.Bits_ALEDataType;
import org.rifidi.edge.ale.datatypes.builtin.formats.BitsFormats;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author kyle
 *
 */
public class BitsDataTypeTest {
	
	private static String bits_1="00110101";
	private static String hexbits_1 = "8:x35";
	
	private static String bits_2 = "0100101101111";
	private static String hexbits_2 = "13:x096F";
	
	@Test
	public void testBitVectorConstructor(){
		BitVector bv = new BitVector(bits_1, 2);
		Bits_ALEDataType dt = new Bits_ALEDataType(bv);
		String answer = dt.getData(BitsFormats.HEX);
		Assert.assertEquals(hexbits_1, answer);
		
		bv = new BitVector(bits_2, 2);
		dt = new Bits_ALEDataType(bv);
		answer = dt.getData(BitsFormats.HEX);
		Assert.assertEquals(hexbits_2, answer);
	}
	
	@Test
	public void testHexBitsConstructor(){
		Bits_ALEDataType dt = new Bits_ALEDataType(hexbits_1);
		String returnString = dt.getData(BitsFormats.HEX);
		Assert.assertEquals(hexbits_1, returnString);
		
		dt = new Bits_ALEDataType(hexbits_2);
		returnString = dt.getData(BitsFormats.HEX);
		Assert.assertEquals(hexbits_2, returnString);
	}

}
