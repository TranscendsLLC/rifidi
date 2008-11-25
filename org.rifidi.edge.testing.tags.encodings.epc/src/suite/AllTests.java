/*
 *  AllTests.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package suite;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;
import tests.PCBitsTest;
import tests.bugs.FieldLengthBug;
import tests.encodings.GIAI96;
import tests.encodings.GRAI96Test;
import tests.encodings.NonEPCEncodedTagTest;
import tests.encodings.SGLN96Test;
import tests.encodings.SGTIN96Test;
import tests.encodings.SSCC96;

/**
 * @author kyle
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for suite");
		//$JUnit-BEGIN$
		suite.addTest(new JUnit4TestAdapter(PCBitsTest.class));
		suite.addTest(new JUnit4TestAdapter(NonEPCEncodedTagTest.class));
		suite.addTest(new JUnit4TestAdapter(GIAI96.class));
		suite.addTest(new JUnit4TestAdapter(GRAI96Test.class));
		suite.addTest(new JUnit4TestAdapter(SGLN96Test.class));
		suite.addTest(new JUnit4TestAdapter(SSCC96.class));
		suite.addTest(new JUnit4TestAdapter(SGTIN96Test.class));
		suite.addTest(new JUnit4TestAdapter(FieldLengthBug.class));
		//$JUnit-END$
		return suite;
	}

}
