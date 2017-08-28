/**
 * 
 */
package org.rifidi.edge.rest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;

/**
 * Restart the edge server.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class RestletRestart implements Runnable {

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try {
			logger.info("Restarting the Rifidi Server");
			Thread.sleep(2000);
			Runtime.getRuntime().exec("sleep 10; /etc/init.d/rifidiserver start");
			final BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
			if (bundleContext != null) {
				try {
					bundleContext.getBundle(0).stop();
				} catch (BundleException e) {
					logger.warn("Exception occurred when attempting to shutdown the root bundle");
				}
			}
		} catch (Exception e) {
			logger.warn("Exception occurred when attempting to restart server");
		} finally {
			System.exit(0);
		}
	}

}
