/*
 *  EPCFactoryTest.java
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

import org.junit.Test;
import org.rifidi.edge.tags.encodings.epc.data.EPCFactory;
import org.rifidi.edge.tags.encodings.epc.data.PCBits;
import org.rifidi.edge.tags.encodings.epc.exceptions.CannotConvertEPCException;

/**
 * @author kyle
 * 
 */
public class EPCFactoryTest {

	public static String BITS = "001100000000100000110011101100101101110111011001000000010100000000110101000001010000000000000000";
	public static String PURE = "urn:epc:id:sgtin:0867360217.005.889520128";
	public static String TAG = "urn:epc:tag:sgtin-96:0.0867360217.005.889520128";
	public static String RAW = "urn:epc:raw:96.x300833B2DDD9014035050000";

	@Test
	public void testToPureFromBits() {
		EPCFactory factory = new EPCFactory();
		try {
			Assert.assertEquals(PURE, factory.toPureFromBitString(BITS));
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}

	@Test
	public void testToPureFromGen2() {

		// convert from bits to pure.
		EPCFactory factory = new EPCFactory();
		try {
			PCBits pcBits = new PCBits(BITS.length());
			Assert.assertEquals(PURE, factory.toPureFromG2Memory(BITS, pcBits
					.toString()));
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}

		// convert from bits to pure. make sure only correct number of bits are
		// read
		try {
			PCBits pcBits = new PCBits(BITS.length());
			Assert.assertEquals(PURE, factory.toPureFromG2Memory(BITS
					+ "10101011010101", pcBits.toString()));
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}

	@Test
	public void testToPureURIFromTagURI() {
		EPCFactory factory = new EPCFactory();
		try {
			Assert.assertEquals(PURE, factory.toPureFromTag(TAG));
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}

	@Test
	public void testToTagURIFromBits() {
		EPCFactory factory = new EPCFactory();
		try {
			Assert.assertEquals(TAG, factory.toTagFromBitString(BITS));
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}

	@Test
	public void testToTagFromGen2() {

		// convert from bits to pure.
		EPCFactory factory = new EPCFactory();
		try {
			PCBits pcBits = new PCBits(BITS.length());
			Assert.assertEquals(TAG, factory.toTagFromG2Memory(BITS, pcBits
					.toString()));
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}

		// convert from bits to pure. make sure only correct number of bits are
		// read
		try {
			PCBits pcBits = new PCBits(BITS.length());
			Assert.assertEquals(TAG, factory.toTagFromG2Memory(BITS
					+ "10101011010101", pcBits.toString()));
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}

	@Test
	public void testToBitsFromTagURI() {
		EPCFactory factory = new EPCFactory();
		try {
			Assert.assertEquals(BITS, factory.toBitStringFromTag(TAG));
		} catch (CannotConvertEPCException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testToBitsFromRaw() {
		EPCFactory factory = new EPCFactory();
		try {
			Assert.assertEquals(BITS, factory.toBitStringFromRaw(RAW));
		} catch (CannotConvertEPCException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

	@Test
	public void testToRawFromBits() {
		EPCFactory factory = new EPCFactory();
		Assert.assertEquals(RAW, factory.toRawFromBitString(BITS, true));
	}

	@Test
	public void testToG2MemFromBits() {
		EPCFactory factory = new EPCFactory();

		PCBits pcBits = new PCBits(BITS.length());
		Assert.assertEquals(pcBits.toString()+BITS, factory.toG2MemoryFromBitString(BITS));

	}
	
	@Test
	public void testToG2MemFromRaw(){
		EPCFactory factory = new EPCFactory();

		PCBits pcBits = new PCBits(BITS.length());
		try {
			Assert.assertEquals(pcBits.toString()+BITS, factory.toG2MemoryFromRaw(RAW));
		} catch (CannotConvertEPCException e) {
			e.printStackTrace();
			Assert.fail();
		}

	}
}
