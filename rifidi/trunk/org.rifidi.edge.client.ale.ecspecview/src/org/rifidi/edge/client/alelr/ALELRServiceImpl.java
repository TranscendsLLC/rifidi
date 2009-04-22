/**
 * 
 */
package org.rifidi.edge.client.alelr;

import java.util.HashSet;
import java.util.Set;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * Not threadsafe because we only use this class inside of eclipse!!!!
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ALELRServiceImpl implements ALELRService, ALEService {
	/** Stubs that are connected to the remote SOAP server. */
	private ALELRServicePortType lrServicePortType = null;
	private ALEServicePortType aleServicePortType = null;
	/** Set containing all the alelrListeners. */
	private Set<ALELRListener> alelrListeners;
	/** Set containing all the aleListeners. */
	private Set<ALEListener> aleListeners;

	/**
	 * Constructor.
	 */
	public ALELRServiceImpl() {
		// connect to the soap service
		JaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();
		lrFactory.setServiceClass(ALELRServicePortType.class);
		// get address from preferences store
		lrFactory.setAddress(Activator.getDefault().getPreferenceStore()
				.getString(Activator.ALELR_ENDPOINT));
		lrServicePortType = (ALELRServicePortType) lrFactory.create();
		

		JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
		aleFactory.setServiceClass(ALEServicePortType.class);
		aleFactory.setAddress(Activator.getDefault().getPreferenceStore()
				.getString(Activator.ALE_PORT_URL_PREF_NAME));
		aleServicePortType = (ALEServicePortType) aleFactory.create();
		
		alelrListeners = new HashSet<ALELRListener>();
		aleListeners = new HashSet<ALEListener>();
		// see if it works
		try {
			EmptyParms parms = new EmptyParms();
			lrServicePortType.getVendorVersion(parms);
		} catch (Throwable e) {
			lrServicePortType = null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Activator.ALELR_ENDPOINT)) {
			updateALELR(event);
			return;
		}
		if (event.getProperty().equals(Activator.ALE_PORT_URL_PREF_NAME)) {
			updateALEReading(event);
			return;
		}
	}

	private void updateALELR(PropertyChangeEvent event) {
		// darn, can't reuse the factory
		JaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();
		lrFactory.setServiceClass(ALELRServicePortType.class);
		// use new address
		lrFactory.setAddress((String) event.getNewValue());
		lrServicePortType = (ALELRServicePortType) lrFactory.create();
		// see if it works
		try {
			lrServicePortType.getVendorVersion(new EmptyParms());
		} catch (Throwable e) {
			lrServicePortType = null;
		}
		for (ALELRListener listener : alelrListeners) {
			listener.setALELRStub(lrServicePortType);
		}
	}

	private void updateALEReading(PropertyChangeEvent event) {
		JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
		aleFactory.setServiceClass(ALEServicePortType.class);
		aleFactory.setAddress(Activator.getDefault().getPreferenceStore()
				.getString(Activator.ALE_PORT_URL_PREF_NAME));
		aleServicePortType = (ALEServicePortType) aleFactory.create();
		// see if it works
		try {
			aleServicePortType
					.getVendorVersion(new org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms());
		} catch (Throwable e) {
			aleServicePortType = null;
		}
		for (ALEListener listener : aleListeners) {
			listener.setALEStub(aleServicePortType);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALELRService#registerALELRListener(org.rifidi
	 * .edge.client.alelr.ALELRListener)
	 */
	@Override
	public void registerALELRListener(ALELRListener listener) {
		if (lrServicePortType != null) {
			listener.setALELRStub(lrServicePortType);
		}
		alelrListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALELRService#unregisterALELRListener(org
	 * .rifidi.edge.client.alelr.ALELRListener)
	 */
	@Override
	public void unregisterALELRListener(ALELRListener listener) {
		alelrListeners.remove(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#registerALEListener(org.rifidi
	 * .edge.client.alelr.ALEListener)
	 */
	@Override
	public void registerALEListener(ALEListener listener) {
		if (aleServicePortType != null) {
			listener.setALEStub(aleServicePortType);
		}
		aleListeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#unregisterALEListener(org.rifidi
	 * .edge.client.alelr.ALEListener)
	 */
	@Override
	public void unregisterALEListener(ALEListener listener) {
		aleListeners.remove(listener);
	}

}
