/**
 * 
 */
package org.rifidi.edge.core.provisioning;

import java.io.File;
import java.net.MalformedURLException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.obr.Repository;
import org.osgi.service.obr.RepositoryAdmin;
import org.osgi.service.obr.Requirement;
import org.osgi.service.obr.Resolver;
import org.osgi.service.obr.Resource;

/**
 * @author Jochen Mader - jochen@pramari.com
 *
 */
public class Activator implements BundleActivator {

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	@Override
	public void start(BundleContext arg0) throws Exception {
		RepositoryAdmin repositoryAdmin = (RepositoryAdmin) arg0.getService(arg0.getServiceReference(RepositoryAdmin.class.getName()));
		try {
			repositoryAdmin.addRepository((new File(
					"/home/jochen/Desktop/obr.xml")).toURI().toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		Resolver resolver = repositoryAdmin.resolver();
		resolver.add(repositoryAdmin.discoverResources("(&(symbolicname=de.pflanzenmoerder.prof1))")[0]);
		if (resolver.resolve()) {
			resolver.deploy(true);
		} else {
			Requirement[] reqs = resolver.getUnsatisfiedRequirements();
			for (int i = 0; i < reqs.length; i++) {
				System.out.println("Unable to resolve: " + reqs[i].getFilter());
			}
		}
		// resolver.add(resource);
		// repositoryAdmin.
	}

	/* (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	@Override
	public void stop(BundleContext arg0) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
