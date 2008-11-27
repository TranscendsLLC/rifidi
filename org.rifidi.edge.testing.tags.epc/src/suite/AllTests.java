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

import tests.c1g2.EPCMemoryBankTest;
import tests.c1g2.EPCMemoryBankTestWithoutHeaderNSI;
import tests.c1g2.EPCMemoryBankWithoutHeaderTest;
import tests.c1g2.ReservedMemoryBankTest;
import tests.c1g2.TIDMemoryBankTest;
import tests.c1g2.UserMemoryBankTest;
import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author kyle
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for suite");
		//$JUnit-BEGIN$
		suite.addTest(new JUnit4TestAdapter(ReservedMemoryBankTest.class));
		suite.addTest(new JUnit4TestAdapter(EPCMemoryBankTest.class));
		suite.addTest(new JUnit4TestAdapter(EPCMemoryBankWithoutHeaderTest.class));
		suite.addTest(new JUnit4TestAdapter(EPCMemoryBankTestWithoutHeaderNSI.class));
		suite.addTest(new JUnit4TestAdapter(TIDMemoryBankTest.class));
		suite.addTest(new JUnit4TestAdapter(UserMemoryBankTest.class));
		//$JUnit-END$
		return suite;
	}

}
