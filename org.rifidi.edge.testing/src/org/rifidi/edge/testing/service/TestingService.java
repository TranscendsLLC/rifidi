package org.rifidi.edge.testing.service;

/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public interface TestingService {
	/**
	 * Tells the Testing Service to run the tests in a separate thread.
	 * @param classes JUnit 4 tests
	 */
	public void addJunitTests(Class<?>...classes);
}
