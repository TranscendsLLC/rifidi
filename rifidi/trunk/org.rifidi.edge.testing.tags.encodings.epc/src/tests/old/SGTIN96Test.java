/*
 *  SGTIN96Test.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.old;

import org.junit.Test;

/**
 * Test conversions in the SGTIN-96 format
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SGTIN96Test extends AbstractEncodingTest {

	public static String BITS = "001100000000100000110011101100101101110111011001000000010100000000110101000001010000000000000000";
	public static String PURE = "urn:epc:id:sgtin:0867360217.005.889520128";
	public static String TAG = "urn:epc:tag:sgtin-96:0.0867360217.005.889520128";
	public static String RAW = "urn:epc:raw:96.x300833B2DDD9014035050000";

	public SGTIN96Test() {
		super(BITS, RAW, PURE, TAG);
	}

	@Test
	public void testFromBits() {
		super.testFromBits();
	}

	@Test
	public void testFromPure() {
		super.testFromPure();
	}

	@Test
	public void testFromTag() {
		super.testFromTag();
	}

	@Test
	public void testFromRaw() {
		super.testFromRaw();
	}

}
