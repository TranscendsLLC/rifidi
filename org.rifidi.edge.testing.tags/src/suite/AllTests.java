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

import tests.BitVectorTest;
import tests.ConvertingUtilTest;
import tests.MemoryBankTest;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author kyle
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for org.rifidi.edge.testing.tags");
		//$JUnit-BEGIN$
		suite.addTest(new JUnit4TestAdapter(BitVectorTest.class));
		suite.addTest(new JUnit4TestAdapter(MemoryBankTest.class));
		suite.addTest(new JUnit4TestAdapter(ConvertingUtilTest.class));
		//$JUnit-END$
		return suite;
	}

}
