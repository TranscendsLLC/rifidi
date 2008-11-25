/*
 *  NonEPCToTagURI.java
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
import org.junit.Test;
import org.rifidi.edge.tags.encodings.epc.exceptions.CannotConvertEPCException;

/**
 * This Junit tests the conversion from a non-epc encoded tag to the raw URI
 * format, as described in procedure 5.4 of TDS 1.4
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class NonEPCEncodedTagTest {
	
	private static String LENGTH = "00001";
	private static String RFU="00";
	private static String TOGGLE = "1";
	private static String AFI = "11111111";
	private static String TAG_ID = "1000"+ "1011" + "1101" + "0100";
	private static String PCBITS = LENGTH + RFU + TOGGLE + AFI;
	private static String RAW_URI = "urn:epc:raw:16.xFF.x8BD4";
	
	@Test
	public void testToTagURI(){
		EPC epc = new EPC(TAG_ID, PCBITS);
		try {
			String s = epc.toTagURI();
			Assert.assertEquals(RAW_URI, s);
		} catch (CannotConvertEPCException e) {
			Assert.fail();
		}
	}
	
	@Test
	public void testToGen2(){
		EPC epc = new EPC(RAW_URI, PCBITS);
		try{
			String s = epc.toGen2Memory();
			Assert.assertEquals(PCBITS+TAG_ID, s);
		}catch(CannotConvertEPCException e){
			Assert.fail();
		}
	}
	
	

}
