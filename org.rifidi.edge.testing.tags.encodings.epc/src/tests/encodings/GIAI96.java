/*
 *  GIAI96.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.encodings;

import org.junit.Test;

/**
 * Test conversions in the GIAI-96 format
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public class GIAI96 extends AbstractEncodingTest {

	private static String BITS = "00110100" + "000" + "000" + "0000001101001110011010000011010010011101" + "000010110111010110000101101000100100111011";
	private static String TAG = "urn:epc:tag:giai-96:0.014200353949.196865329467";
	private static String PURE = "urn:epc:id:giai:014200353949.196865329467";
	private static String RAW = "urn:epc:raw:96.x34000D39A0D2742DD616893B";
	
	public GIAI96() {
		super(BITS, RAW, PURE, TAG);
		// TODO Auto-generated constructor stub
	}
	
	@Test
	public void testFromBits() {
		super.testFromBits();
	}

	@Test
	public void testFromTag() {
		super.testFromTag();
	}

	@Test
	public void testFromRaw() {
		super.testFromRaw();
	}
	
	@Test
	public void testFromGen2(){
		super.testFromG2TagMem();
	}

}
