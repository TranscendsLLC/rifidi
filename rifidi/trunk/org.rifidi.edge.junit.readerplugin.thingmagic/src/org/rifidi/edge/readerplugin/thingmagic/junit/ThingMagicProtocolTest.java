package org.rifidi.edge.readerplugin.thingmagic.junit;

import java.util.Arrays;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.rifidi.edge.core.exception.readerConnection.RifidiInvalidMessageFormat;
import org.rifidi.edge.readerPlugin.thingmagic.ThingMagicProtocol;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class ThingMagicProtocolTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testToObject() {
		ThingMagicProtocol protocol = new ThingMagicProtocol();
		
		
		byte[] output = {'0','x','2','F','3','3','A','7','2','3','0','0','8',
						 'C','9','0','7','C','F','4','7','6','1','B','F','D','\n','\n'};
		
		
		
		Object test = null;
		
		for (byte b: output){
			try {
				test = protocol.add(b);
			} catch (RifidiInvalidMessageFormat e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		Assert.assertTrue(test.equals("0x2F33A723008C907CF4761BFD"));
		
		
//		byte[] output2 = new byte[output.length * 2];
//		
//		System.arraycopy(output, 0, output2, 0, output.length);
//		System.arraycopy(output, 0, output2, output.length, output.length);
//		
//		test = protocol.toObject(output2);
//		
//		Assert.assertTrue(test.size() == 2);
//		Assert.assertTrue(test.get(0).equals("0x2F33A723008C907CF4761BFD"));
//		Assert.assertTrue(test.get(1).equals("0x2F33A723008C907CF4761BFD"));
		
	}

	@Test
	public void testFromObject() {
		byte[] test = {'0','x','2','F','3','3','A','7','2','3','0','0','8',
				 'C','9','0','7','C','F','4','7','6','1','B','F','D','\n'};
		
		ThingMagicProtocol protocol = new ThingMagicProtocol();
		
		
		
		String input = "0x2F33A723008C907CF4761BFD\n";
		
		byte[] output = null;
		try {
			output = protocol.toByteArray((Object) input);
		} catch (RifidiInvalidMessageFormat e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Assert.assertTrue(Arrays.equals(output, test));
		
	}

}
