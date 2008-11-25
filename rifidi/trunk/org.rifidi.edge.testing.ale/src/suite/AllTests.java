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

import tests.datatypes.BitsDataTypeTest;
import tests.datatypes.EPCDataTypeTest;
import tests.datatypes.UintDataTypeTests;
import tests.fields.AFI_ALEFieldTests;
import tests.fields.AccessPwd_ALEFieldTests;
import tests.fields.EPCBank_ALEFieldTests;
import tests.fields.Fixed_ALEFieldTests;
import tests.fields.KillPwd_ALEFieldTests;
import tests.fields.NSI_ALEFieldTests;
import tests.fields.TIDBank_ALEFieldTests;
import tests.fields.UserBank_ALEFieldTests;
import tests.fields.Variable_ALEFieldTests;
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
		suite.addTest(new JUnit4TestAdapter(BitsDataTypeTest.class));
		suite.addTest(new JUnit4TestAdapter(EPCDataTypeTest.class));
		suite.addTest(new JUnit4TestAdapter(UintDataTypeTests.class));
		
		suite.addTest(new JUnit4TestAdapter(EPCDataTypeTest.class));
		suite.addTest(new JUnit4TestAdapter(KillPwd_ALEFieldTests.class));
		suite.addTest(new JUnit4TestAdapter(AccessPwd_ALEFieldTests.class));
		suite.addTest(new JUnit4TestAdapter(EPCBank_ALEFieldTests.class));
		suite.addTest(new JUnit4TestAdapter(TIDBank_ALEFieldTests.class));
		suite.addTest(new JUnit4TestAdapter(UserBank_ALEFieldTests.class));
		suite.addTest(new JUnit4TestAdapter(AFI_ALEFieldTests.class));
		suite.addTest(new JUnit4TestAdapter(NSI_ALEFieldTests.class));
		suite.addTest(new JUnit4TestAdapter(Fixed_ALEFieldTests.class));
		suite.addTest(new JUnit4TestAdapter(Variable_ALEFieldTests.class));
		//$JUnit-END$
		return suite;
	}

}
