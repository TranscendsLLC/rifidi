/*
 *  EPCTest.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package tests.old;

import junit.framework.Assert;

import org.junit.Test;
import org.rifidi.edge.tags.encodings.epc.exceptions.CannotConvertEPCException;


/**
 * @author kyle
 *
 */
public class EPCTest {
	
	@Test
	public void testFromBits(){
		String inbound = "001100000000100000110011101100101101110111011001000000010100000000110101000001010000000000000000";
		EPC epc = new EPC(inbound);
		try {
			Assert.assertEquals(inbound, epc.toBitString());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testFromPureURI(){
		String inbound = "urn:epc:id:sgtin:0867360217.005.889520128";
		EPC epc = new EPC(inbound);
		try {
			Assert.assertEquals(inbound, epc.toPureURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testFromTagURI(){
		String inbound = "urn:epc:tag:sgtin-96:0.0867360217.005.889520128";
		EPC epc = new EPC(inbound);
		try {
			Assert.assertEquals(inbound, epc.toTagURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testFromRawURI(){
		String inbound = "urn:epc:raw:96.x300833B2DDD9014035050000";
		EPC epc = new EPC(inbound);
		try {
			Assert.assertEquals(inbound, epc.toRawURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testRawURI(){
		String inbound = "00000";
		String answer = "urn:epc:raw:5.x00";
		EPC epc = new EPC(inbound);
		try {
			Assert.assertEquals(answer, epc.toRawURI());
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}
}
