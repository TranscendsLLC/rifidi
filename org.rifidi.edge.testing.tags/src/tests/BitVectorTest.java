/*
 *  BitVectorTest.java
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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.tags.util.BitVector;

/**
 * @author kyle
 *
 */
public class BitVectorTest{

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testSize(){
		String testString="0001";
		BitVector bv = new BitVector(testString, 2);
		Assert.assertEquals(testString.length(), bv.bitLength());
		
		testString="10001";
		bv = new BitVector(testString, 2);
		Assert.assertEquals(testString.length(), bv.bitLength());
		
		testString="4";
		bv = new BitVector(testString, 10);
		Assert.assertEquals(3, bv.bitLength());
		
		testString = "0";
		bv = new BitVector(testString, 10);
		Assert.assertEquals(1, bv.bitLength());
		
		testString="FF";
		bv = new BitVector(testString, 16);
		Assert.assertEquals(8, bv.bitLength());
		
		testString = "11";
		bv = new BitVector(testString, 16);
		Assert.assertEquals(5, bv.bitLength());
	}
	
	@Test
	public void testToString(){
		String testString="0001";
		BitVector bv = new BitVector(testString, 2);
		Assert.assertEquals(testString, bv.toString(2));
		
		testString="10001";
		bv = new BitVector(testString, 2);
		Assert.assertEquals(testString, BitVector.toString(bv, 2));
		
		testString="4";
		bv = new BitVector(testString, 10);
		Assert.assertEquals(testString, BitVector.toString(bv, 10));
		
		testString = "0";
		bv = new BitVector(testString, 10);
		Assert.assertEquals(testString, BitVector.toString(bv, 10));
		
		testString = "ff";
		bv = new BitVector(testString, 16);
		Assert.assertEquals(testString, BitVector.toString(bv, 16));
	}
	
	@Test
	public void testGetSingleBit(){
		String testString = "0100";
		BitVector bv = new BitVector(testString, 2);
		
		Assert.assertEquals(true, bv.get(2));
		Assert.assertEquals(false, bv.get(3));
	}
	
	@Test
	public void testGetBits(){
		String testString = "01110";
		String resultString = "111";
		BitVector bitVector = new BitVector(testString, 2);
		BitVector result = bitVector.get(1, 4);
		Assert.assertEquals(resultString, result.toString(2));
		
		testString = "01110000";
		resultString = "000";
		bitVector = new BitVector(testString, 2);
		result = bitVector.get(1, 4);
		Assert.assertEquals(resultString, BitVector.toString(result, 2));
	}
	
	@Test
	public void testIntValue(){
		String testString = "1001";
		int retValue = 9;
		
		BitVector bv = new BitVector(testString, 2);
		Assert.assertEquals(retValue, bv.intValue());
		
		testString = "5";
		retValue = 5;
		bv = new BitVector(testString, 10);
		Assert.assertEquals(retValue, bv.intValue());
	}
	
	@Test
	public void testEnsureSize(){
		String testString = "0101";
		int size = 10;
		BitVector bv = new BitVector(testString, 2, size);
		Assert.assertEquals(size, bv.bitLength());
		
		testString = "5";
		String result = "0000000101";
		bv = new BitVector(testString, 10, 10);
		Assert.assertEquals(result, bv.toString(2));
	}

}
