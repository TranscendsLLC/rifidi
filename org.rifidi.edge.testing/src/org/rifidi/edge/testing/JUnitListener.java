package org.rifidi.edge.testing;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;


/**
 * @author Jerry Maine - jerry@pramari.com
 *
 */
public class JUnitListener extends RunListener {
	private static final Log logger = LogFactory.getLog(JUnitListener.class);
	
	public void testFinished(Description description){
		logger.debug("JUnit Finished: " + description);
	}
	
	public void testFailure(Failure failure){
		logger.error("JUnit Failure: " + failure);
		//logger.error(failure.getMessage());
		logger.error("JUnit Failure: " + failure.getTrace());
	}
	
	public void testRunFinished(Result result) {
		logger.debug("JUnits that ran: " + result.getRunCount());
		logger.debug("JUnit runtime: " + ((double) result.getRunTime() / 1000) + " second(s)") ;
		
		if (result.wasSuccessful()) {
			logger.debug("No Junits failed.");
		} else {
			logger.error("JUnits that failed: " + result.getFailureCount());
			List<Failure> failures = result.getFailures();
			for (Failure failure: failures){
				logger.error("JUnit Failure: " + failure);
				//logger.error("JUnit Failure (Stack Trace): " + failure.getTrace());
			}
		}
	}
}
