/**
 * 
 */
package org.rifidi.edge.client.alelr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.ui.PlatformUI;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ArrayOfString;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.GetECSpec;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.GetSubscribers;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.InvalidURIExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Subscribe;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Unsubscribe;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.Define;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImmutableReaderExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.InUseExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.Undefine;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ValidationExceptionResponse;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec;
import org.rifidi.edge.client.ale.ecspecview.Activator;
import org.rifidi.edge.client.alelr.decorators.LRSpecDecorator;

/**
 * Not threadsafe because we only use this class inside of eclipse!!!!
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ALELRServiceImpl implements ALELRService, ALEService {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(ALELRServiceImpl.class);
	/** Stubs that are connected to the remote SOAP server. */
	private ALELRServicePortType lrServicePortType = null;
	private ALEServicePortType aleServicePortType = null;
	/** Set containing all the alelrListeners. */
	private Set<ALELRListener> alelrListeners;
	/** Set containing all the aleListeners. */
	private Set<ALEListener> aleListeners;
	/** Viewers registered to the model. */
	private Set<Viewer> viewers;
	/** Viewers registered to the model. */
	private Set<Viewer> aleViewers;

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
		viewers = new HashSet<Viewer>();
		aleViewers = new HashSet<Viewer>();
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
		for (Viewer viewer : viewers) {
			viewer.setInput(lrServicePortType);
			viewer.refresh();
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
		if (aleServicePortType != null) {
			List<ALEServicePortType> serviceList = new ArrayList<ALEServicePortType>();
			serviceList.add(aleServicePortType);
			for (Viewer viewer : aleViewers) {
				viewer.setInput(serviceList);
				viewer.refresh();
			}
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALELRService#registerViewer(org.eclipse.
	 * jface.viewers.Viewer)
	 */
	@Override
	public void registerALELRViewer(Viewer viewer) {
		viewers.add(viewer);
		if (lrServicePortType != null) {
			viewer.setInput(lrServicePortType);
			viewer.refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALELRService#unregisterViewer(org.eclipse
	 * .jface.viewers.Viewer)
	 */
	@Override
	public void unregisterALELRViewer(Viewer viewer) {
		viewers.remove(viewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#registerALEViewer(org.eclipse
	 * .jface.viewers.Viewer)
	 */
	@Override
	public void registerALEViewer(Viewer viewer) {
		aleViewers.add(viewer);
		if (aleServicePortType != null) {
			List<ALEServicePortType> serviceList = new ArrayList<ALEServicePortType>();
			serviceList.add(aleServicePortType);
			viewer.setInput(serviceList);
			viewer.refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#unregisterALEViewer(org.eclipse
	 * .jface.viewers.Viewer)
	 */
	@Override
	public void unregisterALEViewer(Viewer viewer) {
		aleViewers.remove(viewer);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.alelr.ALELRService#getAvailableReaderNames()
	 */
	@Override
	public List<String> getAvailableReaderNames() {
		try {
			return lrServicePortType.getLogicalReaderNames(new EmptyParms())
					.getString();
		} catch (SecurityExceptionResponse e) {
			logger.fatal(e);
		} catch (ImplementationExceptionResponse e) {
			logger.fatal(e);
		}
		return Collections.emptyList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALELRService#deleteReader(org.rifidi.edge
	 * .client.alelr.decorators.LRSpecDecorator)
	 */
	@Override
	public void deleteReader(LRSpecDecorator reader)
			throws InUseExceptionResponse, NoSuchNameExceptionResponse {
		Undefine undefine = new Undefine();
		undefine.setName(reader.getName());
		try {
			lrServicePortType.undefine(undefine);
		} catch (SecurityExceptionResponse e) {
			logger.fatal(e);
		} catch (InUseExceptionResponse e) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR
					| SWT.OK);
			messageBox.setMessage(e.toString());
			messageBox.open();
		} catch (ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (ImmutableReaderExceptionResponse e) {
		} catch (NoSuchNameExceptionResponse e) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR
					| SWT.OK);
			messageBox.setMessage(e.toString());
			messageBox.open();
		}
		for (Viewer viewer : viewers) {
			viewer.setInput(lrServicePortType);
			viewer.refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALELRService#createReader(java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void createReader(String name, List<String> readerNames)
			throws DuplicateNameExceptionResponse, ValidationExceptionResponse {
		Define define = new Define();
		define.setName(name);
		LRSpec spec = new LRSpec();
		spec.setIsComposite(true);
		LRSpec.Readers readers = new LRSpec.Readers();
		readers.getReader().addAll(readerNames);
		spec.setReaders(readers);
		define.setSpec(spec);
		try {
			lrServicePortType.define(define);
		} catch (SecurityExceptionResponse e) {
			logger.fatal(e);
		} catch (ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (DuplicateNameExceptionResponse e) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR
					| SWT.OK);
			messageBox.setMessage(e.toString());
			messageBox.open();
		} catch (ValidationExceptionResponse e) {
			MessageBox messageBox = new MessageBox(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), SWT.ICON_ERROR
					| SWT.OK);
			messageBox.setMessage(e.toString());
			messageBox.open();
		}
		for (Viewer viewer : viewers) {
			viewer.setInput(lrServicePortType);
			viewer.refresh();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#createECSpec(java.lang.String,
	 * org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec)
	 */
	@Override
	public void createECSpec(String name, ECSpec spec)
			throws ECSpecValidationExceptionResponse,
			org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateNameExceptionResponse {
		org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Define define = new org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Define();
		define.setSpec(spec);
		define.setSpecName(name);
		try {
			aleServicePortType.define(define);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse e) {
			logger.fatal(e);
		}
		updateALE();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#deleteECSpec(java.lang.String)
	 */
	@Override
	public void deleteECSpec(String specName)
			throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse {
		org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Undefine undefine = new org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Undefine();
		undefine.setSpecName(specName);
		try {
			aleServicePortType.undefine(undefine);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse e) {
			logger.fatal(e);
		}
		updateALE();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.alelr.ALEService#getECSpec(java.lang.String)
	 */
	@Override
	public ECSpec getECSpec(String specName)
			throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse {
		GetECSpec getEcSpec = new GetECSpec();
		getEcSpec.setSpecName(specName);
		try {
			return aleServicePortType.getECSpec(getEcSpec);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse e) {
			logger.fatal(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#getSubscribers(java.lang.String)
	 */
	@Override
	public ArrayOfString getSubscribers(String specName)
			throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse {
		GetSubscribers subs = new GetSubscribers();
		subs.setSpecName(specName);
		try {
			return aleServicePortType.getSubscribers(subs);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse e) {
			logger.fatal(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.alelr.ALEService#getAvailableECSpecNames()
	 */
	@Override
	public List<String> getAvailableECSpecNames() {
		try {
			return aleServicePortType
					.getECSpecNames(
							new org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.EmptyParms())
					.getString();
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse e) {
			logger.fatal(e);
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#subscribeECSpec(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void subscribeECSpec(String specName, String uri)
			throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse,
			InvalidURIExceptionResponse, DuplicateSubscriptionExceptionResponse {
		Subscribe subs = new Subscribe();
		subs.setNotificationURI(uri);
		subs.setSpecName(specName);
		try {
			aleServicePortType.subscribe(subs);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse e) {
			logger.fatal(e);
		}
		updateALE();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.alelr.ALEService#unsubscribeECSpec(java.lang.String
	 * , java.lang.String)
	 */
	@Override
	public void unsubscribeECSpec(String specName, String uri)
			throws org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse,
			InvalidURIExceptionResponse, NoSuchSubscriberExceptionResponse {
		Unsubscribe unsubs = new Unsubscribe();
		unsubs.setNotificationURI(uri);
		unsubs.setSpecName(specName);
		try {
			aleServicePortType.unsubscribe(unsubs);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse e) {
			logger.fatal(e);
		} catch (org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse e) {
			logger.fatal(e);
		}
		updateALE();
	}

	private void updateALE(){
		if (aleServicePortType != null) {
			List<ALEServicePortType> serviceList = new ArrayList<ALEServicePortType>();
			serviceList.add(aleServicePortType);
			for (Viewer viewer : aleViewers) {
				viewer.setInput(serviceList);
				viewer.refresh();
			}
		}
	}
}
