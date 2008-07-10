package org.rifidi.edge.testing;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class JunitListener extends RunListener {
	private static final Log logger = LogFactory.getLog(JunitListener.class);
	
	public void testFinished(Description description){
		logger.debug("JUnit Finished: " + description);
	}
	
	public void testFailure(Failure failure){
		logger.error(failure);
		logger.error(failure.getMessage());
		logger.error(failure.getTrace());
	}
}
