/**
 * 
 */
package org.rifidi.edge.rest;

import java.util.LinkedList;
import java.util.List;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.restlet.Component;

/**
 * @author matt
 *
 */
public class Activator implements BundleActivator {
	
	private static List<Component> components = new LinkedList<Component>();

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext arg0) throws Exception {
		
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext arg0) throws Exception {
		for(Component comp:components) {
			comp.stop();
		}
	}
	
	public static void addToComponents(Component comp) {
		components.add(comp);
	}

}
