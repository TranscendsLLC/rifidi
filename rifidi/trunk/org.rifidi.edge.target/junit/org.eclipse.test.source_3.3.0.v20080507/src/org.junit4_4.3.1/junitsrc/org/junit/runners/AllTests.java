package org.junit.runners;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import junit.framework.Test;
import org.junit.internal.runners.OldTestClassRunner;

/** Runner for use with JUnit 3.8.x-style AllTests classes
 * (those that only implement a static <code>suite()</code>
 * method). For example:
 * <pre>
 * &#064;RunWith(AllTests.class)
 * public class ProductTests {
 *    public static junit.framework.Test suite() {
 *       ...
 *    }
 * }
 * </pre>
 */
public class AllTests extends OldTestClassRunner {
	@SuppressWarnings("unchecked")
	public AllTests(Class<?> klass) throws Throwable {
		super(testFromSuiteMethod(klass));
	}

	public static Test testFromSuiteMethod(Class<?> klass) throws Throwable {
		Method suiteMethod= null;
		Test suite= null;
		try {
			suiteMethod= klass.getMethod("suite");
			if (! Modifier.isStatic(suiteMethod.getModifiers())) {
				throw new Exception(klass.getName() + ".suite() must be static");
			}
			suite= (Test) suiteMethod.invoke(null); // static method
		} catch (InvocationTargetException e) {
			throw e.getCause();
		}
		return suite;
	}
}
