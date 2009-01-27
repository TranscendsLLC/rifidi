package org.rifidi.edge.core.rmi;

import java.rmi.RemoteException;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.edge.core.rmi.api.readerconnection.EdgeServerStub;
import org.rifidi.edge.core.rmi.api.readerconnection.ReaderPluginManagerStub;
import org.rifidi.edge.core.rmi.readerconnection.impl.EdgeServerStubImpl;
import org.rifidi.edge.core.rmi.readerconnection.impl.ReaderPluginManagerStubImpl;
import org.rifidi.edge.core.rmi.service.RMIServerService;
import org.rifidi.edge.core.rmi.service.impl.RMIServerServiceImpl;

/**
 * Activator for the RMI Bundle
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class Activator implements BundleActivator {

	private RMIServerServiceImpl rmiServerService;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("== Bundle RMI started ==");

		System.out.println("Registering Service: RMISeverService");
		rmiServerService = new RMIServerServiceImpl();
		context.registerService(RMIServerService.class.getName(),
				rmiServerService, null);
		rmiServerService.start();

		try {
			EdgeServerStubImpl edgeServerStub = new EdgeServerStubImpl();
			rmiServerService.bindToRMI(edgeServerStub, EdgeServerStub.class
					.getName());
		} catch (RemoteException e) {
			System.out.println("Cannot export "
					+ EdgeServerStub.class.getName());
		}

		try {
			ReaderPluginManagerStubImpl readerPluginManagerStub = new ReaderPluginManagerStubImpl();
			// stub = readerPluginManagerStub;
			rmiServerService.bindToRMI(readerPluginManagerStub,
					ReaderPluginManagerStub.class.getName());
		} catch (RemoteException e) {
			System.out.println("Cannot export "
					+ ReaderPluginManagerStub.class.getName());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		rmiServerService.unbindFromRMI(ReaderPluginManagerStub.class.getName());
		rmiServerService.unbindFromRMI(EdgeServerStub.class.getName());
		
		rmiServerService.stop();
		System.out.println("== Bundle RMI stopped ==");
	}

}
