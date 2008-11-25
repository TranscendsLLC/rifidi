/*
 *  AbstractEncodingTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.old;

import org.junit.Assert;
import org.rifidi.edge.tags.encodings.epc.data.PCBits;
import org.rifidi.edge.tags.encodings.epc.exceptions.CannotConvertEPCException;

/**
 * 
 * This method serves as an abstract base class for junits that test conversions
 * of a particular EPC encoding scheme, such as SGTIN-96. To use it call the
 * constructor and supply four strings in each of the four formats (bits, pure,
 * tag, and raw) that are for the same tag.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractEncodingTest {

	/**
	 * The bits representation for this tag
	 */
	private String BITS;

	/**
	 * The raw representation for this tag
	 */
	private String RAW;

	/**
	 * The Pure URI representation for this tag
	 */
	private String PURE;

	/**
	 * The Tag URI representation for this tag
	 */
	private String TAG;

	/**
	 * The constructor for the tests for this encoding type. Supply the four
	 * representations for a tag encoded in the scheme
	 * 
	 * @param bits
	 *            the tag in bits format
	 * @param raw
	 *            the tag in raw format
	 * @param pure
	 *            the tag in pure URI format
	 * @param tag
	 *            the tag in tag URI format
	 */
	public AbstractEncodingTest(String bits, String raw, String pure, String tag) {
		BITS = bits;
		RAW = raw;
		PURE = pure;
		TAG = tag;
	}

	/**
	 * Test the convertions from BITS -> BITS, BITS->PURE, BITS->TAG, BITS->RAW,
	 * and BITS->GEN2TagMem
	 */
	public void testFromBits() {
		EPC epc = new EPC(BITS);
		PCBits pcBits = new PCBits(BITS.length());

		try {
			Assert.assertEquals(BITS, epc.toBitString());
		} catch (CannotConvertEPCException e1) {
			Assert.fail();
		}
		try {
			Assert.assertEquals(PURE, epc.toPureURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
		try {
			Assert.assertEquals(TAG, epc.toTagURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
		try {
			Assert.assertEquals(RAW, epc.toRawURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
		try {
			Assert.assertEquals(pcBits + BITS, epc.toGen2Memory());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}

	/**
	 * Test the conversions from PURE->PURE. Make sure that PURE->BITS
	 * PURE->TAG, PURE->RAW, and PURE->GEN2TagMem all fail
	 */
	public void testFromPure() {
		EPC epc = new EPC(PURE);
		boolean failedToBits = false;
		boolean failedToTag = false;
		boolean failedToRaw = false;
		boolean failedToGen2TagMem = false;
		try {
			Assert.assertEquals(BITS, epc.toBitString());
		} catch (CannotConvertEPCException e1) {
			failedToBits = true;
		}
		try {
			Assert.assertEquals(PURE, epc.toPureURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
		try {
			Assert.assertEquals(TAG, epc.toTagURI());
		} catch (CannotConvertEPCException e) {
			failedToTag = true;
		}
		try {
			Assert.assertEquals(RAW, epc.toRawURI());
		} catch (CannotConvertEPCException e) {
			failedToRaw = true;
		}
		try {
			epc.toGen2Memory();
		} catch (CannotConvertEPCException e) {
			failedToGen2TagMem = true;
		}
		Assert.assertTrue(failedToBits);
		Assert.assertTrue(failedToTag);
		Assert.assertTrue(failedToRaw);
		Assert.assertTrue(failedToGen2TagMem);
	}

	/**
	 * Test the conversion from TAG->BITS, TAG->PURE, TAG->TAG. Make sure
	 * TAG->RAW fails
	 */
	public void testFromTag() {
		EPC epc = new EPC(TAG);
		PCBits pcBits = new PCBits(TAG.length());
		boolean failedToRaw = false;
		try {
			Assert.assertEquals(BITS, epc.toBitString());
		} catch (CannotConvertEPCException e1) {
			Assert.fail();
		}
		try {
			Assert.assertEquals(PURE, epc.toPureURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
		try {
			Assert.assertEquals(TAG, epc.toTagURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
		try {
			Assert.assertEquals(RAW, epc.toRawURI());
		} catch (CannotConvertEPCException e) {
			failedToRaw = true;
		}try{
			Assert.assertEquals(pcBits+BITS, epc.toGen2Memory());
		}catch (CannotConvertEPCException e) {
			Assert.fail();
		}
		Assert.assertTrue(failedToRaw);
	}

	/**
	 * Test convesions from RAW->RAW. Make sure RAW->BITS, RAW->TAG, and
	 * RAW->PURE fail
	 */
	public void testFromRaw() {
		EPC epc = new EPC(RAW);
		boolean failedToBits = false;
		boolean failedToTag = false;
		boolean failedToPure = false;
		boolean failedToGen2TagMem = false;
		try {
			Assert.assertEquals(BITS, epc.toBitString());
		} catch (CannotConvertEPCException e1) {
			failedToBits = true;
		}
		try {
			Assert.assertEquals(PURE, epc.toPureURI());
		} catch (CannotConvertEPCException e) {
			failedToPure = true;
		}
		try {
			Assert.assertEquals(TAG, epc.toTagURI());
		} catch (CannotConvertEPCException e) {
			failedToTag = true;
		}
		try {
			Assert.assertEquals(RAW, epc.toRawURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}try{
			epc.toGen2Memory();
		}catch (CannotConvertEPCException e){
			failedToGen2TagMem=true;
		}
		Assert.assertTrue(failedToBits);
		Assert.assertTrue(failedToTag);
		Assert.assertTrue(failedToPure);
		Assert.assertTrue(failedToGen2TagMem);
	}

}
