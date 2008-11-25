/*
 *  PCBitsTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests;

import java.math.BigInteger;

import junit.framework.Assert;

import org.junit.Test;
import org.rifidi.edge.tags.encodings.epc.data.PCBits;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class PCBitsTest {

	public static String LENGTH_BITS = "00011";
	public static String RFU_BITS = "01";
	public static String TOGGLE_BIT = "1";
	public static String AFI_BITS = "01011000";
	public static boolean TOGGLE_BIT_VALUE = true;
	public static String PC_BITS = LENGTH_BITS + RFU_BITS + TOGGLE_BIT
			+ AFI_BITS;

	@Test
	public void testPCBitsConstructor() {
		PCBits pc = new PCBits(PC_BITS);
		Assert.assertEquals(PC_BITS, pc.getBits().toString(2));
	}

	@Test
	public void testLengthBitConstructor() {
		String lengthBits = "01010";
		String otherBits = "00000000000";
		int numBlocks = new BigInteger(lengthBits, 2).intValue();
		PCBits pc = new PCBits(numBlocks * 16);
		Assert.assertEquals(lengthBits + otherBits, pc.getBits().toString(2));

		lengthBits = "00000";
		pc = new PCBits(0);
		Assert.assertEquals(lengthBits + otherBits, pc.getBits().toString(2));
	}

	@Test
	public void testGetLengthBits() {
		PCBits pcBits = new PCBits(PC_BITS);
		Assert.assertEquals(LENGTH_BITS, pcBits.getLengthBits().toString(2));
	}
	
	@Test
	public void testGetRFUBIts(){
		PCBits pcBits = new PCBits(PC_BITS);
		Assert.assertEquals(RFU_BITS, pcBits.getRFUBits().toString(2));
	}
	
	@Test
	public void testGetToggleBit(){
		PCBits pcBits = new PCBits(PC_BITS);
		Assert.assertEquals(TOGGLE_BIT_VALUE, pcBits.getToggleBit());
	}
	
	@Test
	public void testGetAFIBits(){
		PCBits pcBits = new PCBits(PC_BITS);
		Assert.assertEquals(AFI_BITS, pcBits.getAFIBits().toString(2));
	}
	
	@Test
	public void testGetLength(){
		PCBits pcBits = new PCBits(PC_BITS);
		int numBits = new BigInteger(LENGTH_BITS, 2).intValue();
		Assert.assertEquals(numBits*16, pcBits.getLength());
	}

}
