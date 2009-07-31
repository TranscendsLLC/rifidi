/**
 * 
 */
package org.rifidi.edge.demo1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public class Activator implements BundleActivator {

	//TODO: UGLY!!!!!!!!!!!!!
	public volatile static DemoController instance;
	
	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext arg0) throws Exception {
		instance.stop();
	}

}
